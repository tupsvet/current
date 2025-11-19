package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CurrencyConverter extends JFrame {
    private JComboBox<String> fromCurrencyComboBox;
    private JComboBox<String> toCurrencyComboBox;
    private JTextField amountField;
    private JLabel resultLabel;

    // ОШИБКИ ДЛЯ ДИНАМИЧЕСКОГО АНАЛИЗА:

    // ОШИБКА 1: Утечка памяти - статический список никогда не очищается
    private static final List<byte[]> MEMORY_LEAK_LIST = new ArrayList<>();

    // ОШИБКА 2: Проблема многопоточности - несинхронизированный доступ
    private int threadUnsafeCounter = 0;

    // ОШИБКА 3: Медленная кэш-таблица (неэффективная структура данных)
    private List<String> inefficientCache = new ArrayList<>();

    // ОШИБКА 4: Бесполезные вычисления (нагрузка на CPU)
    private Random random = new Random();

    public CurrencyConverter() {
        setupUI();
    }

    private void setupUI() {
        setTitle("Конвертер валют - Демо динамических ошибок");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);

        amountField = new JTextField("100", 10);
        resultLabel = new JLabel("0.00");
        resultLabel.setForeground(Color.BLUE);

        String[] currencies = {"USD", "EUR", "GBP", "JPY", "RUB"};
        fromCurrencyComboBox = new JComboBox<>(currencies);
        toCurrencyComboBox = new JComboBox<>(currencies);
        toCurrencyComboBox.setSelectedItem("EUR");

        // Основные кнопки
        JButton convertButton = new JButton("Конвертировать");
        JButton clearButton = new JButton("Очистить");

        // Кнопки для тестирования ошибок
        JButton memoryLeakButton = new JButton("💾 Утечка памяти");
        JButton slowOperationButton = new JButton("🐌 Медленная операция");
        JButton threadIssueButton = new JButton("⚡ Проблема потоков");
        JButton inefficientCacheButton = new JButton("📊 Неэффективный кэш");

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Сумма:"));
        panel.add(amountField);
        panel.add(new JLabel("Из:"));
        panel.add(fromCurrencyComboBox);
        panel.add(new JLabel("В:"));
        panel.add(toCurrencyComboBox);
        panel.add(new JLabel("Результат:"));
        panel.add(resultLabel);
        panel.add(convertButton);
        panel.add(clearButton);
        panel.add(memoryLeakButton);
        panel.add(slowOperationButton);
        panel.add(threadIssueButton);
        panel.add(inefficientCacheButton);

        add(panel);

        // Обработчики событий
        convertButton.addActionListener(e -> convert());
        clearButton.addActionListener(e -> {
            amountField.setText("");
            resultLabel.setText("0.00");
        });

        // ОШИБКА 1: УТЕЧКА ПАМЯТИ
        memoryLeakButton.addActionListener(e -> causeMemoryLeak());

        // ОШИБКА 2: МЕДЛЕННАЯ ОПЕРАЦИЯ В UI ПОТОКЕ
        slowOperationButton.addActionListener(e -> simulateSlowOperation());

        // ОШИБКА 3: ПРОБЛЕМА МНОГОПОТОЧНОСТИ
        threadIssueButton.addActionListener(e -> startThreadRaceCondition());

        // ОШИБКА 4: НЕЭФФЕКТИВНЫЙ КЭШ
        inefficientCacheButton.addActionListener(e -> useInefficientCache());

        // ОШИБКА 5: ДУБЛИРОВАННЫЕ СЛУШАТЕЛИ (утечка)
        convertButton.addActionListener(e -> duplicateListener()); // Дубликат!
    }

    // ОШИБКА 1: УТЕЧКА ПАМЯТИ - статический список постоянно растет
    private void causeMemoryLeak() {
        // Каждый вызов добавляет 2MB в статический список (никогда не очищается)
        for (int i = 0; i < 200; i++) {
            MEMORY_LEAK_LIST.add(new byte[1024 * 10]); // 10KB × 200 = 2MB
        }

        // Добавляем еще некоторые объекты для разнообразия утечек
        for (int i = 0; i < 50; i++) {
            MEMORY_LEAK_LIST.add(new byte[1024 * 50]); // Дополнительные 2.5MB
        }

        resultLabel.setText(String.format("Утечка: %dMB добавлено",
                (MEMORY_LEAK_LIST.size() * 10) / 1024));

        // Выводим в консоль для отслеживания
        System.out.println("Memory leak: " + MEMORY_LEAK_LIST.size() + " objects, ~" +
                (MEMORY_LEAK_LIST.size() * 10 / 1024) + "MB");
    }

    // ОШИБКА 2: МЕДЛЕННАЯ ОПЕРАЦИЯ В UI ПОТОКЕ - блокирует интерфейс
    private void simulateSlowOperation() {
        resultLabel.setText("Начинаем медленную операцию...");

        // Имитация тяжелых вычислений в UI потоке
        long total = 0;
        for (long i = 0; i < 500000000L; i++) { // Очень много итераций
            total += i % 97; // Бесполезные вычисления
            // Периодически обновляем UI чтобы видеть "зависание"
            if (i % 10000000 == 0) {
                resultLabel.setText("Обработано: " + (i / 1000000) + "M итераций");
                // Принудительное обновление UI
                resultLabel.paintImmediately(resultLabel.getBounds());
            }
        }

        resultLabel.setText("Медленная операция завершена: " + total);
    }

    // ОШИБКА 3: ПРОБЛЕМА МНОГОПОТОЧНОСТИ - гонка потоков
    private void startThreadRaceCondition() {
        resultLabel.setText("Запуск 15 потоков...");

        // Запускаем много потоков с несинхронизированным доступом
        for (int i = 0; i < 15; i++) {
            final int threadId = i;
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    threadUnsafeCounter++; // RACE CONDITION!

                    // Имитация работы
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Периодически обновляем UI из другого потока (опасно!)
                    if (j % 200 == 0) {
                        SwingUtilities.invokeLater(() -> {
                            resultLabel.setText("Поток " + threadId + ": " + threadUnsafeCounter);
                        });
                    }
                }
            }).start();
        }
    }

    // ОШИБКА 4: НЕЭФФЕКТИВНАЯ СТРУКТУРА ДАННЫХ И АЛГОРИТМ
    private void useInefficientCache() {
        resultLabel.setText("Наполнение неэффективного кэша...");

        // Добавляем много данных в неэффективную структуру
        for (int i = 0; i < 10000; i++) {
            inefficientCache.add("cache_entry_" + i + "_" +
                    System.currentTimeMillis() + "_" + random.nextDouble());
        }

        // Неэффективный поиск в ArrayList (O(n) вместо O(1))
        int found = 0;
        for (int i = 0; i < 1000; i++) {
            String searchFor = "cache_entry_" + random.nextInt(10000);
            if (inefficientCache.contains(searchFor)) { // Медленный поиск!
                found++;
            }
        }

        resultLabel.setText("Кэш: " + inefficientCache.size() + " записей, найдено: " + found);
    }

    // ОШИБКА 5: ДУБЛИРОВАННЫЙ СЛУШАТЕЛЬ (вызывается дважды)
    private void duplicateListener() {
        // Этот метод будет вызываться дважды при конвертации
        System.out.println("Дублированный слушатель вызван в: " + System.currentTimeMillis());
    }

    // ОШИБКА 6: УТЕЧКА ЧЕРЕЗ СТАТИЧЕСКИЕ ССЫЛКИ
    private void setupStaticLeak() {
        // Создаем объект, который держит ссылку на внешний ресурс
        MemoryHungryObject obj = new MemoryHungryObject();
        MEMORY_LEAK_LIST.add(obj.getData());
    }

    // Вспомогательный класс для утечек
    private class MemoryHungryObject {
        private byte[] data = new byte[1024 * 100]; // 100KB

        public byte[] getData() {
            return data;
        }
    }

    private void convert() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            String from = (String) fromCurrencyComboBox.getSelectedItem();
            String to = (String) toCurrencyComboBox.getSelectedItem();

            if (from == null || to == null) {
                resultLabel.setText("Ошибка выбора валюты");
                return;
            }

            // Простая логика конвертации
            double rateFrom = getRate(from);
            double rateTo = getRate(to);
            double result = (amount / rateFrom) * rateTo;

            resultLabel.setText(String.format("%.2f %s", result, to));

        } catch (NumberFormatException e) {
            resultLabel.setText("Введите число");
        } catch (Exception e) {
            resultLabel.setText("Ошибка расчета");
        }
    }

    private double getRate(String currency) {
        switch (currency) {
            case "USD": return 1.0;
            case "EUR": return 0.85;
            case "GBP": return 0.73;
            case "JPY": return 110.5;
            case "RUB": return 0.011;
            default: return 1.0;
        }
    }

    public static void main(String[] args) {
        // Добавляем паузу для подключения VisualVM
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new CurrencyConverter().setVisible(true);
        });
    }
}
