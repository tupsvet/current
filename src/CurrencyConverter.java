import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class CurrencyConverter extends JFrame {

    private JComboBox<String> fromCurrency, toCurrency;
    private JTextField amountField;
    private JLabel resultLabel;
    private Map<String, Double> rates;
    private DecimalFormat df = new DecimalFormat("#,##0.00");

    public CurrencyConverter() {
        // Инициализация курсов валют
        rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("EUR", 0.85);
        rates.put("GBP", 0.73);
        rates.put("RUB", 73.50);
        rates.put("UAH", 27.20);

        setupUI();
    }

    private void setupUI() {
        setTitle("Конвертер валют");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 200);
        setLocationRelativeTo(null);

        // Создаем компоненты
        amountField = new JTextField("1.00", 10);
        resultLabel = new JLabel("0.00");
        resultLabel.setForeground(Color.BLUE);

        String[] currencies = rates.keySet().toArray(new String[0]);
        fromCurrency = new JComboBox<>(currencies);
        toCurrency = new JComboBox<>(currencies);
        toCurrency.setSelectedItem("EUR");

        JButton convertBtn = new JButton("Конвертировать");
        JButton clearBtn = new JButton("Очистить");

        // Создаем панель
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Добавляем компоненты
        panel.add(new JLabel("Сумма:"));
        panel.add(amountField);
        panel.add(new JLabel("Из:"));
        panel.add(fromCurrency);
        panel.add(new JLabel("В:"));
        panel.add(toCurrency);
        panel.add(new JLabel("Результат:"));
        panel.add(resultLabel);
        panel.add(convertBtn);
        panel.add(clearBtn);

        add(panel);

        // Обработчики событий
        convertBtn.addActionListener(e -> convert());
        clearBtn.addActionListener(e -> {
            amountField.setText("");
            resultLabel.setText("0.00");
        });
        amountField.addActionListener(e -> convert());

        // Первая конвертация
        convert();
    }

    private void convert() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            String from = (String) fromCurrency.getSelectedItem();
            String to = (String) toCurrency.getSelectedItem();

            double result = (amount / rates.get(from)) * rates.get(to);
            resultLabel.setText(df.format(result) + " " + to);

        } catch (Exception e) {
            resultLabel.setText("Ошибка");
        }
    }

    public static void main(String[] args) {
        // Запуск приложения
        SwingUtilities.invokeLater(() -> {
            new CurrencyConverter().setVisible(true);
        });
    }
}