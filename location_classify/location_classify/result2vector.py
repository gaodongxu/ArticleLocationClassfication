# -*- coding: utf-8 -*-
'''
Created on 2018年4月28日

将结果转化为向量形式并保存在文件中

@author: Gdx
'''

import sys
import os
import time
import json
reload(sys)
sys.setdefaultencoding('utf8')

file_path = sys.path[1] + '/file/'
ALL_FEA_LIST = []

def load_fea_list():
        """
        函数功能： 
            读取地域特征词
        """
        try:

            feature_words_f = open(file_path + 'loc_features.txt', 'r')
            
            line_count = 0
            for line in feature_words_f.readlines():
                line = line.strip()
                if len(line) > 0:
                    ALL_FEA_LIST.append(line)
                line_count += 1

        except:
            print 'Error in load_fea_list'
            
            
if __name__ == '__main__':
    
    load_fea_list()
    
    article_file_path = r'D:\data\GraduationDesign\Articles'
    article_id_file_path = r'D:\data\GraduationDesign\ID\res2vec_id'
    
    article_id_file = open(article_id_file_path)
    for article_id in article_id_file.readlines():
        fea_weight_map = {}
        
        article_id = article_id.strip()
        id_len = len(article_id)
        print article_id
        
        article = open(r'D:\data\GraduationDesign\Articles' + '\\' + article_id[5:id_len - 5] + '\\' 
                       + article_id[5:id_len - 3] + '\\' + article_id)
        all_content = article.read()
        article.close()
     
        text = json.loads(all_content)

        loclist_str = text.get('loclist')
        
        for loc_wei_str in loclist_str:
            loc_wei_json = json.loads(loc_wei_str)
            loc = loc_wei_json.get('loc')
            weight = loc_wei_json.get('weight')
            path_str = loc.split('->')
            path_str_len = len(path_str)
            
            if path_str_len > 2:
                word = path_str[2]
            else:
                word = path_str[path_str_len - 1]
            
            if len(word) > 1 and word in ALL_FEA_LIST: 
                index = ALL_FEA_LIST.index(word)
                fea_weight_map[index] = weight
         
        path = r'D:\data\GraduationDesign\Result_Vector' + '\\' + article_id[5:id_len - 5] + '\\' + article_id[5:id_len - 3]
        if not os.path.exists(path):
            os.makedirs(path)
         
        vector_file = open(path + '\\' + article_id, 'w')
        vector_file.write(str(fea_weight_map))
        vector_file.close()
         
    print time.localtime(time.time())
