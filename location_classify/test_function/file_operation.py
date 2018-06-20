# -*- coding: utf-8 -*-
'''
Created on 2018年4月24日

@author: Gdx
'''
import os
import json

# fo = open(r'D:\data\GraduationDesign\Article\2\241\cmpp_241843')
# content = fo.read()
# fo.close()
# print content

file_list = os.listdir(r"D:\data\GraduationDesign\Article\2")
print file_list

for fl in file_list:
    article_list = os.listdir(r'D:\data\GraduationDesign\Article\2' + '\\' + fl)
    print article_list
    for article in article_list:
        article_file = open(r'D:\data\GraduationDesign\Article\2' + '\\' + fl + '\\' + article)
        content = article_file.read()
#         print content
        text = json.loads(content)
        print str(text).decode('raw_unicode_escape')
        print text.get('content')
        print 'ID===' + text.get('ID')
        print 'title===' + text.get('title')
        print 'source===' + text.get('source')
        loclist_str = text.get('loclist')
        print 'loclist===' + str(loclist_str).decode('raw_unicode_escape')
        print loclist_str[0]
        loclist_json = json.loads(loclist_str[0])
        print 'loc===' + loclist_json.get('loc')
        print 'weight===' + str(loclist_json.get('weight'))
