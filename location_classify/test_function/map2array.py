# -*- coding: utf-8 -*-
'''
Created on 2018年4月23日

@author: Gdx
'''
from scipy.sparse import csr_matrix

fea_weight_map={}
fea_weight_map[0]=0.5
fea_weight_map[2]=1.0
fea_weight_map[3]=1.5
fea_weight_map[6]=2.0
fea_weight_map[9]=2.5


if __name__ == '__main__':
    
    row = []
    col = []
    data = []
    print fea_weight_map
    for tu in fea_weight_map.items():
        k = int(tu[0])
        if k < 0:
            fea_weight_map.pop(tu[0])
        else:
            col.append(tu[0])
            data.append(tu[1])
            row.append(0)
        
    input_array = csr_matrix((data, (row, col)), shape=(1,10)).toarray()

    print fea_weight_map
    print input_array
#     prediction = Loc_recognize_class.CLF_MODEL.predict(input_array)
#     pred_label = int(prediction[0]) 
            
            
            