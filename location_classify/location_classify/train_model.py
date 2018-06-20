# -*- coding: utf-8 -*-
'''
Created on 2018年5月1日

训练模型并保存

@author: Gdx
'''
import time
from scipy.sparse import csr_matrix
from sklearn.ensemble import RandomForestClassifier
from sklearn.externals import joblib

if __name__ == '__main__':
    
    tree_number = 90
    
    clf = RandomForestClassifier(n_estimators=tree_number, n_jobs=-1)
    
    article_file_path = r'D:\data\GraduationDesign\Articles'
    train_id_file_path = r'D:\data\GraduationDesign\ID\train_id'
    article_vector_file_path = r'D:\data\GraduationDesign\Article_Vector'
    result_vector_file_path = r'D:\data\GraduationDesign\Result_Vector'
    model_file_path = r"D:\data\GraduationDesign\Model"
    this_model_path = model_file_path + r"\model_" + str(tree_number) + "_none.pkl"
    
    train_vectors = []
    result_vectors = []
    
    print '读取训练数据：' + time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    print '正在读取中......' 
    
    train_id_file = open(train_id_file_path)
    for article_id in train_id_file.readlines():
        article_id = article_id.strip()
        id_len = len(article_id)
#         print article_id
       
        article_vector_path = article_vector_file_path + '\\' + article_id[5:id_len - 5] + '\\' + article_id[5:id_len - 3]
        article_vector_file = open(article_vector_path + '\\' + article_id)
        article_vector_str = article_vector_file.read()
        article_vector_file.close()
        
        result_vector_path = result_vector_file_path + '\\' + article_id[5:id_len - 5] + '\\' + article_id[5:id_len - 3]
        result_vector_file = open(result_vector_path + '\\' + article_id)
        result_vector_str = result_vector_file.read()
        result_vector_file.close()  

        article_vector = eval(article_vector_str)
        result_vector = eval(result_vector_str)
        
        if len(article_vector) <= 0 or len(result_vector) <= 0:
            continue
        
#         print result_vector.items()
        if len(result_vector.items()) > 0 and result_vector.items()[0][1] >= 0.2:
                res = result_vector.items()[0][0]
        else:
            continue
#         print result_vector
        result_vectors.append(res)
        
        row = []
        col = []
        data = []
    
        for tu in article_vector.items():
            k = int(tu[0])
            if k < 0:
                article_vector.pop(tu[0])
            else:
                col.append(tu[0])
                data.append(int(tu[1] * 100))
                row.append(0)
        
        input_array = csr_matrix((data, (row, col)), shape=(1, 10015)).toarray()

#         print article_vector
#         print input_array
        train_vectors.append(input_array[0])
 
#     print '----------'
#     print train_vectors
#     print result_vectors
#     print len(train_vectors)
#     print len(result_vectors)
    
    print '读取训练数据完毕：' + time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    print '开始训练：' + time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    clf.fit(train_vectors, result_vectors)
    joblib.dump(clf, this_model_path)
    print '训练完毕：' + time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    
    
