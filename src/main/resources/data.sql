INSERT INTO model (id, default_value, display_name, formula, label_text, name, trust_rank)
VALUES (1, '0.04,42.13,0.40,0.52,0.93,0.20', 'Модель Чессера',
        '-2.04 - 5.24 * %f + 0.0053 * %f - 6.65 * %f + 4.40 * %f - 0.08 * %f - 0.10 * %f',
        'Параметры (наример: Х1,Х2,Х3,Х4,Х5,Х6):', 'CHESSER', 0.5);
INSERT INTO model_params (model_id, params) VALUES (1, 0.04);
INSERT INTO model_params (model_id, params) VALUES (1, 42.13);
INSERT INTO model_params (model_id, params) VALUES (1, 0.40);
INSERT INTO model_params (model_id, params) VALUES (1, 0.52);
INSERT INTO model_params (model_id, params) VALUES (1, 0.93);
INSERT INTO model_params (model_id, params) VALUES (1, 0.20);

INSERT INTO model (id, default_value, display_name, formula, label_text, name, trust_rank)
VALUES (2, '0.02,1.08,0.70,2.08,3.24', 'Модель Жданова',
        '4.32 - 1.25 * %f - 0.12 * %f - 0.07 * %f - 0.34 * %f - 2.17 * %f',
        'Параметры (наример: Х1,Х2,Х3,Х4,Х5):', 'GDANOV', 0.3);
INSERT INTO model_params (model_id, params) VALUES (2, 0.02);
INSERT INTO model_params (model_id, params) VALUES (2, 1.08);
INSERT INTO model_params (model_id, params) VALUES (2, 0.70);
INSERT INTO model_params (model_id, params) VALUES (2, 2.08);
INSERT INTO model_params (model_id, params) VALUES (2, 3.24);

INSERT INTO model (id, default_value, display_name, formula, label_text, name, trust_rank)
VALUES (3, '-0.006,0.03,4.92', 'Модель Джу-Ха Техонга',
        '0.11 * %f - 0.007 * %f - 0.11 * %f',
        'Параметры (наример: Х1,Х2,Х3):', 'JU', 0.2);
INSERT INTO model_params (model_id, params) VALUES (3, -0.006);
INSERT INTO model_params (model_id, params) VALUES (3, 0.03);
INSERT INTO model_params (model_id, params) VALUES (3, 4.92);
