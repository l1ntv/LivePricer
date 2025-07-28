package ru.rsreu.lint.livepricer.servlet;

import ru.rsreu.lint.livepricer.ResourceLoader;
import ru.rsreu.lint.livepricer.service.PriceService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AveragePriceServlet extends HttpServlet {

    private final PriceService priceService = PriceService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String itemId = req.getParameter(ResourceLoader.getProperty("parameter.itemId"));

        if (itemId == null || itemId.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Введите параметр itemId\"}");
            return;
        }

        double avgPrice = priceService.getAveragePrice(itemId);

        if (avgPrice < 0) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Ошибка базы данных\"}");
        } else if (avgPrice == 0.0) {
            resp.getWriter().write(String.format(
                    "{\"error\":\"Запись не найдена\",\"itemId\":\"%s\"}", itemId));
        } else {
            resp.getWriter().write(String.format(
                    "{\"itemId\":\"%s\",\"averagePrice\":%.2f}", itemId, avgPrice));
        }
    }
}
