package ru.rsreu.lint.livepricer.service;

import ru.rsreu.lint.livepricer.dao.LivePriceDAO;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PriceService {

    private static final PriceService INSTANCE = new PriceService();

    private final LivePriceDAO priceDAO = new LivePriceDAO();

    private final ConcurrentHashMap<String, Double> cache = new ConcurrentHashMap<>();

    private final Lock lock = new ReentrantLock();


    public static PriceService getInstance() {
        return INSTANCE;
    }

    private PriceService() {
    }

    public double getAveragePrice(String itemId) {
        lock.lock();
        try {
            Double cached = cache.get(itemId);
            if (cached != null) {
                return cached;
            }

            double avg = priceDAO.getAveragePrice(itemId);
            if (avg >= 0) {
                cache.put(itemId, avg);
            }
            return avg;
        } catch (SQLException e) {
            return -1; // переделать
        } finally {
            lock.unlock();
        }
    }

    public void updateItemPrice(String itemId, String manufacturer, double price) throws SQLException {
        lock.lock();
        try {
            priceDAO.updateItemPrice(itemId, manufacturer, price);
            double newAvg = priceDAO.getAveragePrice(itemId);
            if (newAvg >= 0)
                cache.put(itemId, newAvg);
            else
                cache.remove(itemId);
        } finally {
            lock.unlock();
        }
    }
}
