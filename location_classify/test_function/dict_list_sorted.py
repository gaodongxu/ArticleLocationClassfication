# -*- coding: utf-8 -*-
if __name__ == '__main__':

    adict = {}
    alist = [3, 11, 222]
    adict['a'] = alist

    alist = [2, 33, 111]
    adict['b'] = alist

    alist = [1, 22, 333]
    adict['c'] = alist

    print adict
    b = sorted(adict.items(), lambda x, y: cmp(x[1][0], y[1][0]))  # 按照list里第一个值排序
    print b
    
    b = sorted(adict.items(), lambda x, y: cmp(x[1][1], y[1][1]))  # 按照list里第二个值排序
    print b
    
    b = sorted(adict.items(), lambda x, y: cmp(x[1][2], y[1][2]))  # 按照list里第三个值排序
    print b
