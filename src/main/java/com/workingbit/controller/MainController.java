package com.workingbit.controller;

import com.workingbit.entity.EnumRiskDegree;
import com.workingbit.entity.Model;
import com.workingbit.service.CalculatorService;
import com.workingbit.service.ModelService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

public class MainController {

    // Инъекции Spring
    @Autowired
    private ModelService modelService;
    @Autowired
    private CalculatorService calculatorService;

    // Инъекции JavaFX
    @FXML
    private Label lblResults;
    @FXML
    private VBox vboxModels;
    @FXML
    private VBox vboxTrustRank;

    private Map<String, Double> calcDetails = new HashMap<>();

    /**
     * ОСНОВНОЕ Инициализация создаем UI на основе данных из базы
     */
    @PostConstruct
    public void init() {
        List<Model> models = modelService.findAll();
        // создаем UI на основе данных из базы
        models.forEach((model) -> {
            // создаем кнопки
            Button button = new Button(model.getDisplayName());
            button.setOnAction((event) -> {
                handleModelAction(model.getName(),
                        model.getDisplayName(),
                        model.getLabelText(),
                        ((String) model.getDefaultValue()));
            });
            vboxModels.getChildren().add(button);
            // создаем оценки экспертов
            Double defaultTrustRank = ((Double) model.getTrustRank());
            TextField textField = new TextField(defaultTrustRank.toString());
            textField.setId(model.getName());
            vboxTrustRank.getChildren().add(textField);
        });
    }

    /**
     * ОСНОВНОЕ Обработчик нажатия на кнопку Расчитать
     *
     * @param event
     */
    @FXML
    public void handleCalcAction(ActionEvent event) {
        // достаем из базы все модели
        List<Model> models = modelService.findAll();
        Map<EnumRiskDegree, Set<String>> enumRiskRankBooleanHashMap = new HashMap<>();

        // пробегаем по всем моделям и вычисляем риски
        models.forEach((model) -> {
            String formula = model.getFormula();
            String preparedFormula = String.format(formula, model.getParams().toArray());
            // вычисляем вероятность
            double z = calculatorService.calculate(preparedFormula);
            double possibility = possibility(z);
            // сохраняем детали расчета
            calcDetails.put(model.getDisplayName(), possibility);
            // выводим детали в консоль
            System.out.println(model.getName() + ": Z = " + z + ", P = " + possibility);
            // группируем модель и вычесленный риск
            putRiskDegreeForModel(enumRiskRankBooleanHashMap, model.getName(), possibility);
        });

        StringBuilder stringBuilder = new StringBuilder();
        // проходим по вычесленным рискам, считаем вероятность банкротсва и создаем строку для отчета
        enumRiskRankBooleanHashMap.forEach((key, value) -> {
            // суммируем риски для модели на основе значений экспертов
            final Double[] sumRank = {0.0};
            value.forEach((s) -> {
                // берем TextField из UI
                Node exp = vboxTrustRank.getChildren()
                        .filtered(node -> node.getId().equalsIgnoreCase(s))
                        .get(0);
                // получаем проценты
                sumRank[0] += Double.valueOf(((TextField) exp).getText()) * 100;
            });
            // формируем строку для отчета
            stringBuilder.append(key.getDisplayName())
                    .append(": ")
                    .append(sumRank[0])
                    .append("%")
                    .append("\n");
        });
        // выводим отчет
        lblResults.setText(stringBuilder.toString());
    }

    /**
     * Обработчик для нажатия на кнопку Детали
     */
    @FXML
    public void handleDetailAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Детали расчета");
        alert.setHeaderText(null);
        StringBuilder stringBuilder = new StringBuilder();
        calcDetails.forEach((key, value) -> {
            double percent = Math.ceil(value * 100);
            stringBuilder.append(key)
                    .append(", P = ")
                    .append(percent)
                    .append("%\n");
        });
        alert.setContentText(stringBuilder.toString());
        alert.showAndWait();
    }

    /**
     * Группируем модель и ее вероятность
     * @param enumRiskDegreeBooleanHashMap
     * @param model
     * @param possibility
     */
    private void putRiskDegreeForModel(Map<EnumRiskDegree, Set<String>> enumRiskDegreeBooleanHashMap,
                                       String model,
                                       double possibility) {
        // ищем в каком интервале находится текущая P
        Optional<EnumRiskDegree> first = Arrays.stream(EnumRiskDegree.values())
                .filter(enumRiskDegree -> enumRiskDegree.inInterval(possibility))
                .findFirst();
        // если нашли кладем в множество для этой модели
        if (first.isPresent()) {
            EnumRiskDegree risk = first.get();
            if (!enumRiskDegreeBooleanHashMap.containsKey(risk)) {
                enumRiskDegreeBooleanHashMap.put(risk, new HashSet<>());
            }
            enumRiskDegreeBooleanHashMap.get(risk).add(model);
            return;
        }
        // выбрасываем исключение
        throw new RuntimeException("Риск не определен");
    }

    /**
     * Расчет вероятности
     * @param z
     * @return
     */
    private double possibility(double z) {
        return 1 / (1 + Math.exp(-z));
    }

    /**
     * Обработчик нажатия на кнопку модели
     * @param modelName
     * @param modelDisplayName
     * @param contentText
     * @param defaultValue
     */
    private void handleModelAction(String modelName, String modelDisplayName, String contentText, String defaultValue) {
        Model model = modelService.findByName(modelName);
        // конвертируем список параметров в строку
        String defaultParamsStringForModel = modelService.getDefaultParamsStringForModel(model.getParams());
        if (defaultParamsStringForModel == null) {
            defaultParamsStringForModel = defaultValue;
        }
        TextInputDialog dialog = new TextInputDialog(defaultParamsStringForModel);
        dialog.setTitle(modelDisplayName);
        dialog.setHeaderText("Введите параметры");
        dialog.setContentText(contentText);

        Optional<String> result = dialog.showAndWait();
        // сохраняем новые параметры в базу
        result.ifPresent((params) -> {
            String[] split = params.split("\\s*,\\s*");
            List<Double> doubles = Arrays.stream(split)
                    .map(Double::valueOf)
                    .collect(Collectors.toList());
            model.setParams(doubles);
            modelService.save(model);
        });
    }
}
