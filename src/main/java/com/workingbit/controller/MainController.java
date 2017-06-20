package com.workingbit.controller;

import com.workingbit.entity.EnumModel;
import com.workingbit.entity.EnumRiskDegree;
import com.workingbit.entity.Model;
import com.workingbit.service.ModelService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

public class MainController {

    // Инъекции Spring
    @Autowired
    private ModelService modelService;

    // Инъекции JavaFX
    @FXML
    private Button btnChesser;
    @FXML
    private Label lblResults;

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
    }

    /**
     * Параметры
     * Х1 = 0,04
     * Х2 = 42,13
     * Х3 = 0,40
     * Х4 = 0,52
     * Х5 = 0,93
     * Х6 = 0,20
     *
     * @param event
     */
    @FXML
    public void handleChesserAction(ActionEvent event) {
        showArgsInputDialog(
                EnumModel.CHESSER,
                "Параметры (наример: Х1,Х2,Х3,Х4,Х5,Х6):",
                "0.04,42.13,0.40,0.52,0.93,0.20"
        );
    }

    /**
     * Х1 = 0,02
     * Х2 = 1,08
     * Х3 = 0,70
     * Х4 = 2,08
     * Х5 = 3,24
     *
     * @param event
     */
    @FXML
    public void handleGdanovAction(ActionEvent event) {
        showArgsInputDialog(
                EnumModel.GDANOV,
                "Параметры (наример: Х1,Х2,Х3,Х4,Х5):",
                "0.02,1.08,0.70,2.08,3.24"
        );
    }

    /**
     * Х1 = -0,006
     * Х2 = 0,03
     * Х3 = 4,92
     *
     * @param event
     */
    @FXML
    public void handleJuAction(ActionEvent event) {
        showArgsInputDialog(
                EnumModel.JU,
                "Параметры (наример: Х1,Х2,Х3):",
                "-0.006,0.03,4.92"
        );
    }

    @FXML
    public void handleCalcAction(ActionEvent event) {
        List<Double> chesserParams = modelService.findByName(EnumModel.CHESSER).getParams();
        double zChesser = -2.04
                - 5.24 * getOrOne(chesserParams, 0)
                + 0.0053 * getOrOne(chesserParams, 1)
                - 6.65 * getOrOne(chesserParams, 2)
                + 4.40 * getOrOne(chesserParams, 3)
                - 0.08 * getOrOne(chesserParams, 4)
                - 0.1 * getOrOne(chesserParams, 5);
        double pChesser = possibility(zChesser);
        List<Double> gdanovParams = modelService.findByName(EnumModel.GDANOV).getParams();

        double zGdanov = 4.32
                - 1.25 * getOrOne(gdanovParams, 0)
                - 0.12 * getOrOne(gdanovParams, 1)
                - 0.07 * getOrOne(gdanovParams, 2)
                - 0.34 * getOrOne(gdanovParams, 3)
                - 2.17 * getOrOne(gdanovParams, 4);
        double pGdanov = possibility(zGdanov);

        List<Double> juParams = modelService.findByName(EnumModel.JU).getParams();
        double zJu = 0.11 * getOrOne(juParams, 0)
                - 0.007 * getOrOne(juParams, 1)
                - 0.11 * getOrOne(juParams, 2);
        double pJu = possibility(zJu);

        HashMap<EnumRiskDegree, Set<EnumModel>> enumRiskDegreeBooleanHashMap = new HashMap<>();
        putRiskDegreeforModel(enumRiskDegreeBooleanHashMap, EnumModel.CHESSER, pChesser);
        putRiskDegreeforModel(enumRiskDegreeBooleanHashMap, EnumModel.GDANOV, pGdanov);
        putRiskDegreeforModel(enumRiskDegreeBooleanHashMap, EnumModel.JU, pJu);

        enumRiskDegreeBooleanHashMap.entrySet()
                .stream()
                .forEach(enumRiskDegreeSetEntry -> {
                    lblResults.setText(lblResults.getText()
                            + "\n" + enumRiskDegreeSetEntry.getKey().getDisplayName()
                            + "\n" + enumRiskDegreeSetEntry.getValue().toString());
                });
    }

    private void putRiskDegreeforModel(HashMap<EnumRiskDegree, Set<EnumModel>> enumRiskDegreeBooleanHashMap,
                                       EnumModel model,
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

    private void showArgsInputDialog(EnumModel modelName, String contentText, String defaultValue) {
        Model model = modelService.findByName(modelName);
        String defaultParamsStringForModel = modelService.getDefaultParamsStringForModel(modelName);
        if (defaultParamsStringForModel == null) {
            defaultParamsStringForModel = defaultValue;
        }
        TextInputDialog dialog = new TextInputDialog(defaultParamsStringForModel);
        dialog.setTitle(modelName.getDisplayName());
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
