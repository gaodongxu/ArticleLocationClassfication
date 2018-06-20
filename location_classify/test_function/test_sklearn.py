# -*- coding: utf-8 -*-
'''
Created on 2018年4月23日

@author: Gdx
'''
from sklearn.ensemble import RandomForestClassifier
from sklearn.externals import joblib
 
if __name__ == '__main__':
    x1 = [[1, 1],
       [1, 0]]
    y1 = [1, 1]
    clf = RandomForestClassifier(n_estimators=70, n_jobs=-1, max_depth=60)
    joblib.dump(clf, "../model/model.pkl")
    clf.fit(x1, y1)
#     x2 = [[0, 1],
#        [0, 0]]
#     y2 = [0, 1]
#     clf.partial_fit(x2, y2)
    z = clf.predict([[1, 0], [0, 0]])
    print z
