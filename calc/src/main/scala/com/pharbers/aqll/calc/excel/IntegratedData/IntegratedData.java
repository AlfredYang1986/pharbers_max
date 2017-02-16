package com.pharbers.aqll.calc.excel.IntegratedData;

import com.pharbers.aqll.calc.util.StringOption;

import java.io.Serializable;

public class IntegratedData implements Serializable {
    private Long hospNum;

    private String hospName;

    private Integer yearAndmonth;

    private String minimumUnit;

    private String minimumUnitCh;

    private String minimumUnitEn = "无";

    private String phaid;

    private String strength;

    private String market1Ch;

    private String market1En = "无";

    private Double sumValue;

    private Double volumeUnit;

    private String segment;

    public Integer getYearAndmonth() {return yearAndmonth;}

    public void setYearAndmonth(Integer yearAndmonth) {this.yearAndmonth = yearAndmonth;}

    public Long getHospNum() {return hospNum;}

    public void setHospNum(Long hospNum) {this.hospNum = hospNum;}

    public String getHospName() {return hospName;}

    public void setHospName(String hospName) {this.hospName = hospName;}

    public String getStrength() {return strength;}

    public void setStrength(String strength) {this.strength = strength;}

    public String getPhaid() {return phaid;}

    public void setPhaid(String phaid) {this.phaid = phaid;}

    public Double getSumValue() {return sumValue;}

    public void setSumValue(Double sumValue) {this.sumValue = sumValue;}

    public Double getVolumeUnit() {return volumeUnit;}

    public void setVolumeUnit(Double volumeUnit) {this.volumeUnit = volumeUnit;}

    public String getMinimumUnit() {return minimumUnit;}

    public void setMinimumUnit(String minimumUnit) {this.minimumUnit = minimumUnit;}

    public String getMinimumUnitCh() {return minimumUnitCh;}

    public void setMinimumUnitCh(String minimumUnitCh) {this.minimumUnitCh = minimumUnitCh;}

    public String getMinimumUnitEn() {return minimumUnitEn;}

    public void setMinimumUnitEn(String minimumUnitEn) {this.minimumUnitEn = minimumUnitEn;}

    public String getMarket1Ch() {return market1Ch;}

    public void setMarket1Ch(String market1Ch) {this.market1Ch = market1Ch;}

    public String getMarket1En() {return market1En;}

    public void setMarket1En(String market1En) {this.market1En = market1En;}

    public String getSegment() {return segment;}

    public void setSegment(String segment) {this.segment = segment;}



    public String sortConditions1() {
        return yearAndmonth.toString() + hospNum.toString() + StringOption.takeStringSpace(minimumUnitCh.toString());
    }

    @Override
    public String toString() {
        return ""+yearAndmonth.toString()+
                             "	"+ StringOption.takeStringSpace(minimumUnit)+
                             "	"+hospNum.toString()+
                             "	"+sumValue.toString()+
                             "	"+volumeUnit.toString()+
                             "hospName = "+hospName.toString();
    }

}