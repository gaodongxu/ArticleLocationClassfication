# -*- coding: utf-8 -*-
'''
Created on 2018年5月1日

@author: Gdx
'''

if __name__ == '__main__':
    a=[0, 0, 2, 0, 3, 0]
    
    fea_wei_map={}
    for i in range(len(a)):
        if a[i]>0:
            fea_wei_map[i]=a[i]    
    print fea_wei_map
        
    
    
    