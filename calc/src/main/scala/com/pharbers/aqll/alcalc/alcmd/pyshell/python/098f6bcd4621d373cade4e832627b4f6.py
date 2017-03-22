# -*- coding: utf-8 -*-
"""
Created on Thu Jan 19 10:21:36 2017

@author: Eric.Zhou
"""
import pandas as pd
from pandas import DataFrame , ExcelWriter
import numpy as np
import re
import xlrd, openpyxl
import os,sys

print "The child will write text to a pipe and "
print "the parent will read the text written by child..."

# file descriptors r, w for reading and writing
r, w = os.pipe()

processid = os.fork()
if processid:
    os.close(w)
    r = os.fdopen(r)
    print "Parent reading"
    str = r.read()
    print "result =", str
    sys.exit(0)
else:
    # This is the child process
    os.close(r)
    w = os.fdopen(w, 'w')
    print "Child writing"

    #filename_cpa  = "201611CPA.xlsx" #sys.argv[1]
    #filename_gycx = "201611GYCX.xlsx" #sys.argv[2]
    #filename_year = "2016" #sys.argv[3]

    filename_cpa  = sys.argv[1] #"201611CPA.xlsx"
    filename_gycx = sys.argv[2] #"201611GYCX.xlsx"
    filename_year = sys.argv[3] #"2016"

    print "参数1：" + filename_cpa
    print "参数2：" + filename_gycx
    print "参数3：" + filename_year


    #---导入CPA文件---#   #n5则代表从文件列表NO.5

    #CPA_Step_6. CPA 2016年11月数据(#8文件 即为201611 mkt+others 不包含 13分子 )
    xlsx_cpa_n8 = pd.ExcelFile(u"/Users/liwei/Downloads/upload/098f6bcd4621d373cade4e832627b4f6/2016/" + filename_cpa
                                   ,encoding='GBK')
    cpa_n8 = xlsx_cpa_n8.parse(u'辉瑞1611')


    #---导入GYCX文件---#   #n5则代表从文件列表NO.5


    #GYCX_Step2. GYCX2016.11辉瑞全部数据(#8文件)
    xlsx_gycx_n8 = pd.ExcelFile(u"/Users/liwei/Downloads/upload/098f6bcd4621d373cade4e832627b4f6/2016/" + filename_gycx
                                   ,encoding='GBK')
    gycx_n8 = xlsx_gycx_n8.parse(u'2016年11月辉瑞337个通用名产品数据',header=0)


    #合并CPA各个年份数据
    cpa_131611 = cpa_n8
    #合并GYCX各个年份数据
    gycx_131611 = gycx_n8


    #为cpa合并文件补充min1列
    cpa_131611_touse = cpa_131611.copy()
    cpa_131611_touse.dtypes
    cpa_131611_touse['PACK_NUMBER'] = cpa_131611_touse['PACK_NUMBER'].astype('string')
    cpa_131611_touse[u'PRODUCT_NAME']=cpa_131611_touse[u"PRODUCT_NAME"].fillna(cpa_131611_touse[u"MOLE_NAME"])
    cpa_131611_touse['min1'] = cpa_131611_touse['PRODUCT_NAME']+cpa_131611_touse['APP2_COD']+cpa_131611_touse['PACK_DES']+cpa_131611_touse['PACK_NUMBER']+cpa_131611_touse['CORP_NAME']
    #为gycx合并文件补充min1列
    gycx_131611_touse = gycx_131611.copy()
    gycx_131611_touse.dtypes
    gycx_131611_touse[u'包装规格'] = gycx_131611_touse[u'包装规格'].astype('string')
    gycx_131611_touse[u'药品商品名']=gycx_131611_touse[u"药品商品名"].fillna(gycx_131611_touse[u"通用名"])
    gycx_131611_touse['min1'] = gycx_131611_touse[u'药品商品名']+gycx_131611_touse[u'剂型']+gycx_131611_touse[u'规格']+gycx_131611_touse[u'包装规格']+gycx_131611_touse[u'生产企业']

    #读取市场标准化Others表,截取min1、min1标准列及通用名
    xlsx_others_mapping = pd.ExcelFile(u"/Users/liwei/Downloads/manage/098f6bcd4621d373cade4e832627b4f6/2016/产品标准化 vs IMS_Pfizer_6市场others.xlsx"
                                   ,encoding='GBK')
    others_mapping = xlsx_others_mapping.parse(u'Sheet1',header=1)
    others_mapping_touse = others_mapping.loc[:,['min1',u'min1_标准',u'通用名']]
    others_mapping_touse = others_mapping_touse.drop_duplicates()


    #----------生成others_panel
    #for i in [['INF'],['AI'], ['LD'], ['HTN'], ['ONC'], ['Pain']]:
    for i in [['INF']]:
        mkt=i[0]
        xlsx_mkt_mapping = pd.ExcelFile(u"/Users/liwei/Downloads/manage/098f6bcd4621d373cade4e832627b4f6/2016/按辉瑞采购清单中的通用名划分6市场others.xlsx",encoding='GBK')
        mkt_mapping = xlsx_mkt_mapping.parse(mkt)#mkt
        mkt_mapping_cpa = mkt_mapping[[u'CPA反馈通用名',u'TA']]
        mkt_mapping_gycx = mkt_mapping[[u'GYCX反馈通用名',u'TA']]
        mkt_mapping_cpa.rename(columns={u'CPA反馈通用名':u'通用名',u'TA':u'marketname'},inplace=True)
        mkt_mapping_gycx.rename(columns={u'GYCX反馈通用名':u'通用名',u'TA':u'marketname'},inplace=True)
        others_mapping_touse_cpa = pd.merge(others_mapping_touse,mkt_mapping_cpa,how='left',on=u'通用名')
        others_mapping_touse_gycx = pd.merge(others_mapping_touse,mkt_mapping_gycx,how='left',on=u'通用名')
        others_mapping_touse_cpa1 = others_mapping_touse_cpa[['min1',u'min1_标准','marketname']]
        others_mapping_touse_gycx1 = others_mapping_touse_gycx[['min1',u'min1_标准','marketname']]

        cpa_131611_touse1 = pd.merge(cpa_131611_touse,others_mapping_touse_cpa1,how='left',on='min1')
        cpa_131611_touse2 = cpa_131611_touse1[~cpa_131611_touse1['marketname'].isnull()]
        CPA_Others_touse2016 = cpa_131611_touse2.loc[cpa_131611_touse2['YEAR']==int(filename_year)]

        #将CPA 2016制作成Panel需要格式
        CPA_Others_touse2016[u'YEAR'] = CPA_Others_touse2016[u'YEAR'].astype(str)
        CPA_Others_touse2016[u'MONTH'] = CPA_Others_touse2016[u'MONTH'].astype("str").apply(lambda x:x.zfill(2))

        CPA_Others_touse2016['Date'] = CPA_Others_touse2016[u'YEAR'] + CPA_Others_touse2016[u'MONTH']
        CPA_Others_panel = CPA_Others_touse2016[['HOSPITAL_CODE','Date','VALUE','STANDARD_UNIT',u'min1_标准']]
        CPA_Others_panel.rename(columns={'HOSPITAL_CODE':'ID','VALUE':'Sales','STANDARD_UNIT':'Units',u'min1_标准':u'Prod_Name'},inplace=True)
        CPA_Others_panel['Prod_CNAME'] = CPA_Others_panel['Prod_Name']
        CPA_Others_panel['Strength'] = CPA_Others_panel['Prod_Name']

        gycx_131611_touse1 = pd.merge(gycx_131611_touse,others_mapping_touse_gycx1,how='left',on='min1')
        gycx_131611_touse2 = gycx_131611_touse1[~gycx_131611_touse1['marketname'].isnull()]
        GYCX_Others_touse2016 = gycx_131611_touse2.loc[gycx_131611_touse2[u'年']==int(filename_year)]
        #导入Panel医院分市场匹配表
        xlsx_hos_mapping = pd.ExcelFile(u"/Users/liwei/Downloads/manage/098f6bcd4621d373cade4e832627b4f6/2016/Panel_hos.xlsx",encoding='GBK')
        hos_mapping = xlsx_hos_mapping.parse(mkt)

        #采用hos_mapping范围的医院制作cpapanel
        CPA_Others_panel_touse = CPA_Others_panel[CPA_Others_panel['ID'].isin(hos_mapping['ID'])]
        CPA_Others_panel_touse1 = pd.merge(CPA_Others_panel_touse,hos_mapping,how='left',on='ID')
        CPA_Others_panel_touse.shape
        CPA_Others_panel_touse1.shape

        #将GYCX 2016制作成Panel需要格式
        GYCX_Others_touse2016[u'年'] = gycx_131611_touse[u'年'].astype("str")
        GYCX_Others_touse2016[u'月'] = GYCX_Others_touse2016[u'月'].astype("str").apply(lambda x:x.zfill(2))
        GYCX_Others_touse2016['Date'] = GYCX_Others_touse2016[u'年'] + GYCX_Others_touse2016[u'月']
        GYCX_Others_panel = GYCX_Others_touse2016[[u'医院编码','Date',u'金额',u'最小制剂单位数量',u'min1_标准']]
        GYCX_Others_panel.rename(columns={u'医院编码':'ID',u'金额':'Sales',u'最小制剂单位数量':'Units',u'min1_标准':u'Prod_Name'},inplace=True)
        GYCX_Others_panel['Prod_CNAME'] = GYCX_Others_panel['Prod_Name']
        GYCX_Others_panel['Strength'] = GYCX_Others_panel['Prod_Name']
        #采用hos_mapping范围的医院制作gycpanel
        GYCX_Others_panel_touse = GYCX_Others_panel[GYCX_Others_panel['ID'].isin(hos_mapping['ID'])]
        GYCX_Others_panel_touse1 = pd.merge(GYCX_Others_panel_touse,hos_mapping,how='left',on='ID')

        print mkt
        print "原先数据的行数"
        print GYCX_Others_panel_touse.shape
        print "做isin处理后现在数据的行数"
        print GYCX_Others_panel_touse1.shape
        #合并CPA15、GYCX15年panel,用ID匹配，避免了医院重合的关系
        CPA_GYCX_Others_panel_16 = pd.concat([CPA_Others_panel_touse1,GYCX_Others_panel_touse1],axis=0)
        CPA_GYCX_Others_panel_16[u'Sales'] = CPA_GYCX_Others_panel_16[u'Sales'].fillna(0)
        CPA_GYCX_Others_panel_16[u'Units'] = CPA_GYCX_Others_panel_16[u'Units'].fillna(0)
        CPA_GYCX_Others_panel_16 = CPA_GYCX_Others_panel_16.fillna('')

        #分类汇总,缩减行数
        col = CPA_GYCX_Others_panel_16.columns.tolist()
        del col[2]
        del col[2]
        CPA_GYCX_Others_panel_16_final=CPA_GYCX_Others_panel_16.groupby(col,as_index=False).agg({u'Sales': 'sum',u'Units':'sum'})
        #调整列序
        CPA_GYCX_Others_panel_16_final = CPA_GYCX_Others_panel_16_final.loc[:,['ID','Hosp_name','Date','Sales','Units'
                                                                               ,'Prod_Name','Prod_CNAME','HOSP_ID','Strength','DOI','DOIE']]
        print mkt
        print CPA_GYCX_Others_panel_16[[u'Sales',u'Units']].sum()
        print CPA_GYCX_Others_panel_16_final[[u'Sales',u'Units']].sum()
        print CPA_GYCX_Others_panel_16.shape
        print CPA_GYCX_Others_panel_16_final.shape

        w.write("CPA_GYCX_Others_panel_"+filename_year+"_"+mkt+".xlsx | ")

        #输出当前市场CPA&GYCX合并16年Panel
        writer=ExcelWriter(u"/Users/liwei/Downloads/upload/panel/CPA_GYCX_Others_panel_"+filename_year+"_"+mkt+".xlsx")#mkt
        CPA_GYCX_Others_panel_16_final.to_excel(writer,sheet_name='Sheet1',encoding="GBK",index=False)
        writer.save()

    w.close()
    print "Child closing"
    sys.exit(0)
