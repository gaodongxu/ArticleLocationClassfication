# -*- coding: utf-8 -*-
'''
Created on 2018年4月28日

@author: Gdx
'''
from scipy.sparse import csr_matrix

if __name__ == '__main__':
     
    article_file_path = r'D:\data\GraduationDesign\Article'
    article_id_file_path = r'D:\data\GraduationDesign\article_id'
     
    article_id_file = open(article_id_file_path)
    for article_id in article_id_file.readlines():
        article_id = article_id.strip()
        id_len = len(article_id)
        print article_id
       
        path = r'D:\data\GraduationDesign\Vector' + '\\' + article_id[5:id_len - 5] + '\\' + article_id[5:id_len - 3]
        vector_file = open(path + '\\' + article_id)
        vector_str = vector_file.read()
        vector_file.close()
        print vector_str

        fea_weight_map = eval(vector_str)
        print fea_weight_map

        row = []
        col = []
        data = []
    
        for tu in fea_weight_map.items():
            k = int(tu[0])
            if k <= 0:
                fea_weight_map.pop(tu[0])
            else:
                col.append(tu[0]-1)
                data.append(tu[1])
                row.append(0)
        
            input_array = csr_matrix((data, (row, col)), shape=(1,10015)).toarray()

            print fea_weight_map
            print input_array