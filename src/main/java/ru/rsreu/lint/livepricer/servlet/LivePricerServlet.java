package ru.rsreu.lint.livepricer.servlet;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.rsreu.lint.livepricer.ResourceLoader;
import ru.rsreu.lint.livepricer.service.PriceService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class LivePricerServlet extends HttpServlet {

    private final PriceService priceService = PriceService.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            handlePost(req, resp);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при обработке запроса");
        }
    }

    private void handlePost(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        if (!"application/json".equalsIgnoreCase(req.getContentType())) {
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                    "Content-Type должен быть application/json");
            return;
        }

        String jsonStr = jsonToString(req).trim();
        if (jsonStr.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Пустой JSON");
            return;
        }

        JSONArray jsonArray = null;
        try {
            jsonArray = parseJSONString(jsonStr);
            if (jsonArray == null)
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверный формат JSON");
        } catch (IOException exception) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ошибка парсинга JSON: " + exception.getMessage());
            return;
        }


        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);

            String itemId = json.optString(ResourceLoader.getProperty("json.product_id"), null);
            String manufacturer = json.optString(ResourceLoader.getProperty("json.manufacturer_name"), null);
            double price = json.optDouble(ResourceLoader.getProperty("json.price"), Double.NaN);

            if (itemId == null || manufacturer == null || Double.isNaN(price)) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Отсутствуют параметры");
                return;
            }

            try {
                priceService.updateItemPrice(itemId, manufacturer, price);
            } catch (SQLException e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка базы данных");
                return;
            }
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("{\"status\":\"success\"}");
    }

    private String jsonToString(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(req.getInputStream(), "UTF-8"))) {

            char[] buffer = new char[1024];
            int bytesRead;
            while ((bytesRead = reader.read(buffer)) > 0) {
                sb.append(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    private JSONArray parseJSONString(String jsonStr) throws IOException {
        JSONArray jsonArray;
        if (jsonStr.startsWith("[")) {
            jsonArray = new JSONArray(jsonStr);
        } else if (jsonStr.startsWith("{")) {
            jsonArray = new JSONArray();
            jsonArray.put(new JSONObject(jsonStr));
        } else
            return null;
        return jsonArray;
    }
}
