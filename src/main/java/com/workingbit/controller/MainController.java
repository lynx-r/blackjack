package com.workingbit.controller;

import com.workingbit.entity.EnumModel;
import com.workingbit.entity.Model;
import com.workingbit.service.ModelService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainController {

    // Инъекции Spring
    @Autowired
    private ModelService modelService;

    // Инъекции JavaFX
    @FXML
    private Button btnChesser;

    // Переменные
    private String chesserDefaultValue;

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
     *
     Х1 = 0,02
     Х2 = 1,08
     Х3 = 0,70
     Х4 = 2,08
     Х5 = 3,24
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
     *
     Х1 = -0,006
     Х2 = 0,03
     Х3 = 4,92
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
