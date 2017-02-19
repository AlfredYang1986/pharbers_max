package com.pharbers.aqll.calc.excel.IntegratedData;

import com.pharbers.aqll.calc.util.StringOption;

import java.io.Serializable;

public class IntegratedData implements Serializable {
    private Long hospNum = 0L;

    private String hospName;

    private Integer yearAndmonth = 0;

    private String minimumUnit;

    private String minimumUnitCh;

    private String minimumUnitEn = "无";

    private String phaid;

    private String strength;

    private String market1Ch;

    private String market1En = "无";

    private Double sumValue = 0.0;

    private Double volumeUnit = 0.0;

    private String segment;

    public Integer getYearAndmonth() {return yearAndmonth;}

    public void setYearAndmonth(Integer yearAndmonth) {this.yearAndmonth = yearAndmonth;}

    public Long getHospNum() {return hospNum;}

    public void setHospNum(Long hospNum) {this.hospNum = hospNum;}

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



    public String sortConditions1() {
        return yearAndmonth.toString() + hospNum.toString() + StringOption.takeStringSpace(minimumUnitCh.toString());
    }

    @Override
    public String toString() {
        return hospNum.toString() + "," +
                hospName.toString() + "," +
                yearAndmonth.toString() + "," +
                minimumUnit.toString() + "," +
                minimumUnitCh.toString() + "," +
                minimumUnitEn.toString() + "," +
                phaid.toString() + "," +
                strength.toString() + "," +
                market1Ch.toString() + "," +
                market1En.toString() + "," +
                sumValue.toString() + "," +
                volumeUnit.toString();
    }

}