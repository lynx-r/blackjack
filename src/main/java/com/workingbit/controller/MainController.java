package com.workingbit.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.workingbit.entity.EnumRiskDegree;
import com.workingbit.entity.Model;
import com.workingbit.service.CalculatorService;
import com.workingbit.service.ModelService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class MainController {

    // Инъекции Spring
    @Autowired
    private ModelService modelService;
    @Autowired
    private CalculatorService calculatorService;

    @Autowired
    private ApplicationContext ctx;

    // Инъекции JavaFX
    @FXML
    private Button btnChesser;
    @FXML
    private Label lblResults;
    @FXML
    private VBox vboxModels;

    // Переменные
    private int CHESSER_PARAMS_LENGTH = 6;

    /**
     * Инициализация контроллера от JavaFX.
     * Метод вызывается после того как FXML загрузчик произвел инъекции полей.
     * <p>
     * Обратите внимание, что имя метода <b>обязательно</b> должно быть "initialize",
     * в противном случае, метод не вызовется.
     * <p>
     * Также на этом этапе еще отсутствуют бины спринга
     * и для инициализации лучше использовать метод,
     * описанный аннотацией @PostConstruct.
     * Который вызовется спрингом, после того,
     * как им будут произведены все оставшиеся инъекции.
     * {@link MainController#init()}
     */
    @FXML
    public void initialize() {
    }

    /**
     * На этом этапе уже произведены все возможные инъекции.
     */
    @PostConstruct
    public void init() {
        Resource modelsJson = ctx.getResource("classpath:models.json");
        InputStream inputStream = null;
        try {
            inputStream = modelsJson.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            Gson gson = new GsonBuilder().create();
            Map<String, Map<String, String>> map = gson.fromJson(bufferedReader, Map.class);
            map.forEach((key, value) -> {
                Button button = new Button(value.get("displayName"));
                button.setOnAction((event) -> {
                    handleModelAction(key,
                            value.get("displayName"),
                            value.get("labelText"),
                            value.get("defaultValue"));
                });
                vboxModels.getChildren().add(button);
                Model model = new Model();
                model.setName(key);
                String[] defaultValues = value.get("defaultValue").split("\\s*,\\s*");
                List<Double> ts = Arrays.stream(defaultValues)
                        .map(Double::valueOf)
                        .collect(Collectors.toList());
                model.setParams(ts);
                model.setFormula(value.get("formula"));
                modelService.save(model);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(inputStream);
    }


    @FXML
    public void handleCalcAction(ActionEvent event) {
        List<Model> models = modelService.findAll();
        Map<EnumRiskDegree, Set<String>> enumRiskDegreeBooleanHashMap = new HashMap<>();

        models.forEach((model) -> {
            String formula = model.getFormula();
            String preparedFormula = String.format(formula, model.getParams().toArray());
            double z = calculatorService.calculate(preparedFormula);
            double possibility = possibility(z);
            putRiskDegreeForModel(enumRiskDegreeBooleanHashMap, model.getName(), possibility);
        });

        enumRiskDegreeBooleanHashMap.forEach((key, value) -> lblResults.setText(lblResults.getText()
                + "\n" + key.getDisplayName()
                + "\n" + value.toString()));
    }

    private void putRiskDegreeForModel(Map<EnumRiskDegree, Set<String>> enumRiskDegreeBooleanHashMap,
                                       String model,
                                       double possibility) {
        Optional<EnumRiskDegree> first = Arrays.stream(EnumRiskDegree.values())
                .filter(enumRiskDegree -> enumRiskDegree.inInterval(possibility))
                .findFirst();
        if (first.isPresent()) {
            EnumRiskDegree risk = first.get();
            if (!enumRiskDegreeBooleanHashMap.containsKey(risk)) {
                enumRiskDegreeBooleanHashMap.put(risk, new HashSet<>());
            }
            enumRiskDegreeBooleanHashMap.get(risk).add(model);
            return;
        }
        throw new RuntimeException("Риск не определен");
    }

    private double possibility(double z) {
        return 1 / (1 + Math.exp(z));
    }

    private double getOrOne(List<Double> params, int num) {
        return params.get(num) == null ? 1 : params.get(num);
    }

    private void handleModelAction(String modelName, String modelDisplayName, String contentText, String defaultValue) {
        Model model = modelService.findByName(modelName);
        String defaultParamsStringForModel = modelService.getDefaultParamsStringForModel(model.getParams());
        if (defaultParamsStringForModel == null) {
            defaultParamsStringForModel = defaultValue;
        }
        TextInputDialog dialog = new TextInputDialog(defaultParamsStringForModel);
        dialog.setTitle(modelDisplayName);
        dialog.setHeaderText("Введите параметры");
        dialog.setContentText(contentText);

        Optional<String> result = dialog.showAndWait();
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
