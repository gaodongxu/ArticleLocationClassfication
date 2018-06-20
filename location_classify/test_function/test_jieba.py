# -*- coding: utf-8 -*-
import jieba.posseg as pseg  
  
words = pseg.cut("北京市有东城区")  
for word, flag in words:  
    print("%s %s" % (word, flag)) 
