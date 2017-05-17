package com.pharbers.aqll.old.calc.alcalc.almodel;


import com.pharbers.aqll.old.calc.util.StringOption;
import com.pharbers.aqll.old.calc.util.export.Excel;

import java.io.Serializable;

public class IntegratedData implements Serializable {

    @Excel(name = "ID", width = 10, precision = 1)
    private Integer hospNum = 0;

    @Excel(name = "Hosp_name", width = 10)
    private String hospName = "无";

    @Excel(name = "Date", width = 10)
    private Integer yearAndmonth = 0;

    @Excel(name = "Prod_Name", width = 10)
    private String minimumUnit = "无";

    @Excel(name = "Prod_CNAME", width = 10)
    private String minimumUnitCh = "无";

    @Excel(skip = true)
    private String minimumUnitEn = "无";

    @Excel(name = "HOSP_ID", width = 10)
    private String phaid = "无";

    @Excel(name = "Strength", width = 10)
    private String strength = "无";

    @Excel(name = "DOI", width = 10)
    private String market1Ch = "无";

    @Excel(name = "DOIE", width = 10)
    private String market1En = "无";

    @Excel(name = "Units", width = 10, precision = 2)
    private Double volumeUnit = 0.0;

    @Excel(name = "Sales", width = 10, precision = 2)
    private Double sumValue = 0.0;

    @Excel(skip = true)
    private String segment;

    public Integer getYearAndmonth() {return yearAndmonth;}

    public void setYearAndmonth(Integer yearAndmonth) {this.yearAndmonth = yearAndmonth;}

    public Integer getHospNum() {return hospNum;}

    public void setHospNum(Integer hospNum) {this.hospNum = hospNum;}

    public String getHospName() {return hospName;}

    public void setHospName(String hospName) {if(!hospName.equals("#N/A")){this.hospName = hospName;} else {this.hospName = "";}}

    public String getStrength() {return strength;}

    public void setStrength(String strength) {if(!strength.equals("#N/A")){this.strength = strength;} else {this.strength = "";}}

    public String getPhaid() {return phaid;}

    public void setPhaid(String phaid) {if(!phaid.equals("#N/A")){this.phaid = phaid;} else {this.phaid = "";}}

    public Double getSumValue() {return sumValue;}

    public void setSumValue(Double sumValue) {this.sumValue = sumValue;}

    public Double getVolumeUnit() {return volumeUnit;}

    public void setVolumeUnit(Double volumeUnit) {this.volumeUnit = volumeUnit;}

    public String getMinimumUnit() {return minimumUnit;}

    public void setMinimumUnit(String minimumUnit) {if(!minimumUnit.equals("#N/A")){this.minimumUnit = minimumUnit;} else {this.minimumUnit = "";}}

    public String getMinimumUnitCh() {return minimumUnitCh;}

    public void setMinimumUnitCh(String minimumUnitCh) {if(!minimumUnitCh.equals("#N/A")){this.minimumUnitCh = minimumUnitCh;} else {this.minimumUnitCh = "";}}

    public String getMinimumUnitEn() {return minimumUnitEn;}

    public void setMinimumUnitEn(String minimumUnitEn) {if(!minimumUnitEn.equals("#N/A")){this.minimumUnitEn = minimumUnitEn;} else {this.minimumUnitEn = "";}}

    public String getMarket1Ch() {return market1Ch;}

    public void setMarket1Ch(String market1Ch) {if(!market1Ch.equals("#N/A")){this.market1Ch = market1Ch;} else {this.market1Ch = "";}}

    public String getMarket1En() {return market1En;}

    public void setMarket1En(String market1En) {if(!market1En.equals("#N/A")){this.market1En = market1En;} else {this.market1En = "";}}

    public String getSegment() {return segment;}

    public void setSegment(String segment) {this.segment = segment;}

    public String sortConditions1() {return yearAndmonth.toString() + hospNum.toString() + StringOption.takeStringSpace(minimumUnitCh.toString());}

    @Override
    public String toString() {
        char c = (char)31;
        return hospNum.toString() + c +
                hospName.toString() + c +
                yearAndmonth.toString() + c +
                minimumUnit.toString() + c +
                minimumUnitCh.toString() + c +
                minimumUnitEn.toString() + c +
                phaid.toString() + c +
                strength.toString() + c +
                market1Ch.toString() + c +
                market1En.toString() + c +
                sumValue.toString() + c +
                volumeUnit.toString();
    }

}