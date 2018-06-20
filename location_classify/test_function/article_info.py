# -*- coding: utf-8 -*-
'''
Created on 2018年5月7日

@author: Gdx
'''
import json

if __name__ == '__main__':
     
    article_file_path = r'D:\data\GraduationDesign\Articles'

    for article_id in ['cmpp_19907861']:
        article_id = article_id.strip()
        id_len = len(article_id)
        print article_id
        article = open(article_file_path + '\\' + article_id[5:id_len - 5] + '\\' 
                       + article_id[5:id_len - 3] + '\\' + article_id)
        all_content = article.read()
        article.close()
     
        text = json.loads(all_content)
         
        ID = text.get('ID')
        title = text.get('title')
        content = text.get('content')
        source = text.get('source')
        
        print '---------------------'
        print title
        print '---------------------'
        print source
        print '---------------------'
        print content




