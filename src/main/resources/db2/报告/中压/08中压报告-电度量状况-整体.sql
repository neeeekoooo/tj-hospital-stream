-- 整体开关柜季度电度量状况
CREATE TABLE MAXIMO.RE_ALL_VOLTAGE_EM_QUARTER
(
    REALLVOLTAGEEMQUARTERID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1 ),
    ASSETYPE                VARGRAPHIC(100 CODEUNITS16),
    PRODUCTMODEL            VARGRAPHIC(100 CODEUNITS16),
    LOCATION                VARGRAPHIC(100 CODEUNITS16),
    PRODUCTMODELC           VARGRAPHIC(100 CODEUNITS16),
    ActiveElectricDegree    DECIMAL(31, 4),
    ReactiveElectricDegree  DECIMAL(31, 4),
    CREATETIME              VARGRAPHIC(50 CODEUNITS16),
    ElectricDegree          DECIMAL(31, 4),
    REACTIVEPERCENT         DECIMAL(31, 4),
    OPINION                 VARGRAPHIC(50 CODEUNITS16),
    PRIMARY KEY (REALLVOLTAGEEMQUARTERID)
);


-- 整体开关柜年度电度量状况
CREATE TABLE MAXIMO.RE_ALL_VOLTAGE_EM_YEAR
(
    REALLVOLTAGEEMYEARID   BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1 ),
    ASSETYPE               VARGRAPHIC(100 CODEUNITS16),
    PRODUCTMODEL           VARGRAPHIC(100 CODEUNITS16),
    LOCATION               VARGRAPHIC(100 CODEUNITS16),
    PRODUCTMODELC          VARGRAPHIC(100 CODEUNITS16),
    ActiveElectricDegree   DECIMAL(31, 4),
    ReactiveElectricDegree DECIMAL(31, 4),
    CREATETIME             VARGRAPHIC(50 CODEUNITS16),
    ElectricDegree         DECIMAL(31, 4),
    REACTIVEPERCENT        DECIMAL(31, 4),
    OPINION                VARGRAPHIC(50 CODEUNITS16),
    PRIMARY KEY (REALLVOLTAGEEMYEARID)
);


-- 整体中压设备-季度总电度量比对
CREATE TABLE MAXIMO.RE_ALL_VOL_EM_COMP_QUARTER
(
    REALLVOLEMCOMPQUARTERID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1 ),
    ASSETYPE                VARGRAPHIC(100 CODEUNITS16),
    PRODUCTMODEL            VARGRAPHIC(100 CODEUNITS16),
    LOCATION                VARGRAPHIC(100 CODEUNITS16),
    PRODUCTMODELC           VARGRAPHIC(100 CODEUNITS16),
    ELECTRICDEGREE          DECIMAL(31, 4),
    CREATETIME              VARGRAPHIC(100 CODEUNITS16),
    COMPARISON              VARGRAPHIC(50 CODEUNITS16),
    RATIO                   DECIMAL(8, 4),
    PRIMARY KEY (REALLVOLEMCOMPQUARTERID)
);

-- 整体中压设备-季度总电度量比对
CREATE TABLE MAXIMO.RE_ALL_VOL_EM_COMP_YEAR
(
    REALLVOLEMCOMPYEARID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1 ),
    ASSETYPE             VARGRAPHIC(100 CODEUNITS16),
    PRODUCTMODEL         VARGRAPHIC(100 CODEUNITS16),
    LOCATION             VARGRAPHIC(100 CODEUNITS16),
    PRODUCTMODELC        VARGRAPHIC(100 CODEUNITS16),
    ELECTRICDEGREE       DECIMAL(31, 4),
    CREATETIME           VARGRAPHIC(100 CODEUNITS16),
    COMPARISON           VARGRAPHIC(50 CODEUNITS16),
    RATIO                DECIMAL(8, 4),
    PRIMARY KEY (REALLVOLEMCOMPYEARID)
);

