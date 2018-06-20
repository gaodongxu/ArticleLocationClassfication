# -*- coding: utf-8 -*-
'''
Created on 2018年5月1日

测试模型准确率

@author: Gdx
'''
import time
from sklearn.externals import joblib
from sklearn.ensemble import RandomForestClassifier
from scipy.sparse import csr_matrix

if __name__ == '__main__':
    
    print '开始测试'
    tree_number = 70
    clf = RandomForestClassifier(n_estimators=tree_number, n_jobs=-1)
    
    article_file_path = r'D:\data\GraduationDesign\Articles'
    article_vector_path = r'D:\data\GraduationDesign\Article_Vector'
    model_file_path = r"D:\data\GraduationDesign\Model"
    this_model_path = model_file_path + r"\model_" + str(tree_number) + "_none.pkl"
    article_id_file_path = r'D:\data\GraduationDesign\ID\predict_id'
    result_vector_path = r'D:\data\GraduationDesign\Result_Vector'
    

    print '开始加载模型:' + time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    clf = joblib.load(this_model_path)
    print '加载模型完毕，开始遍历测试集:' + time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    
    predict_sum = 0
    predict_true = 0
    
    article_id_file = open(article_id_file_path)
    for article_id in article_id_file.readlines():
        
        article_id = article_id.strip()
        id_len = len(article_id)
        
        article_vector_file = open(article_vector_path + '\\' + article_id[5:id_len - 5] + '\\' 
                       + article_id[5:id_len - 3] + '\\' + article_id)
        article_vector_str = article_vector_file.read()
        article_vector_file.close()
        
        article_vector = eval(article_vector_str)
        article_vector_len = len(article_vector)
        if article_vector_len <= 0:
            continue
        
        row = []
        col = []
        data = []
    
        for tu in article_vector.items():
            k = int(tu[0])
            if k <= 0:
                article_vector.pop(tu[0])
            else:
                col.append(tu[0])
                data.append(int(tu[1] * 100))
                row.append(0)
        
        input_array = csr_matrix((data, (row, col)), shape=(1, 10015)).toarray()
        
        # 预测结果
        result = clf.predict(input_array)
        if article_vector_len == 1 and result[0] == 0:
            continue
        predict_sum += 1
        
        # 读取正确结果
        result_vector_file = open(result_vector_path + '\\' + article_id[5:id_len - 5] + '\\' 
                       + article_id[5:id_len - 3] + '\\' + article_id)
        result_vector_str = result_vector_file.read()
        result_vector_file.close()
        
        result_vector = eval(result_vector_str)
        flag = False
        for tu in result_vector.items():
            k = int(tu[0])
            if k == result:
                flag = True
                break
        
        # 输出有用信息 
        print article_id + '======================='
        print 'art_vec = ' + article_vector_str
        print 'result_vector = ' + result_vector_str + str(flag)
        
        if flag:
            predict_true += 1
    
    print '遍历测试集结束:' + time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    print predict_true * 1.0 / predict_sum
    print '测试结束'
