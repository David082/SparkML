# -*- coding: utf-8 -*-
"""
describe : 
author : yu_wei
created on : 2018/1/31
version :
refer :
-- http://blog.csdn.net/yanyanyufei96/article/details/70338632
-- select * from dw_htlbizdb.tmp_ym_hotelrankings_train_set where orderdate = '2017-09-18' and ordergp > 0;
-- xgboost入门与实战（实战调参篇）
http://blog.csdn.net/u011089523/article/details/72812019
"""
import xgboost as xgb
import pandas as pd

features = [
    "cityid",
    "starlicence",
    "star",
    "goldstar",
    "ratingoverall",
    "novoters",
    "customereval",
    "country",
    "province",
    "zone",
    "upper_1cycle_gp",
    "upper_2cycle_gp",
    "upper_3cycle_gp",
    "upper_4cycle_gp",
    "his_28days_gp",
    "his_3months_gp",
    "his_6months_gp",
    "his_9months_gp",
    "his_12months_gp",
    "year",
    "month",
    "weekofyear",
    "quarter",
    "weekdays",
    "holidays",
    "gp_holiday_hotel_heat",
    "gp_holiday_zone_heat",
    "gp_holiday_city_heat",
    "hotel_div_zone_heat_gp",
    "zone_div_city_heat_gp",
    "upper_1cycle_uv",
    "upper_2cycle_uv",
    "upper_3cycle_uv",
    "upper_4cycle_uv",
    "his_28days_uv",
    "his_3months_uv",
    "his_6months_uv",
    "hotel_pic_score",
    "hq_hotel_pic_score",
    "room_pic_score",
    "static_info_score"
]


def xgbModel(train, x, y):
    # xgb_model = xgb.XGBClassifier(n_estimators=10, max_depth=3, objective="reg:linear", silent=False)
    # xgb_model.fit(train.loc[:, x], train.loc[:, y], eval_metric="rmse")
    # xgb.Booster.save_model(xgb_model)
    dtrain = xgb.DMatrix(train.loc[:, x], train.loc[:, y])
    num_round = 100
    params = {
        # 'booster': 'gbtree',
        'objective': 'reg:linear',
        # 'subsample': 0.6,
        # 'colsample_bytree': 0.8,
        'eta': 0.05,
        'max_depth': 5,
        # 'min_child_weight': 1,
        # 'gamma': 0.0,
        'silent': 0,
        'eval_metric': 'rmse'
        # 'eval_metric': 'error'
    }
    bst = xgb.train(params, dtrain, num_round)
    bst.save_model("./data/pyxgb.model")


if __name__ == "__main__":
    y = "ordergp"
    x = features
    train = pd.read_csv("./data/purexgb.csv", sep=",", header="infer")
    print(train.head())
    xgbModel(train, x, y)
