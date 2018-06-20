# -*- coding: utf-8 -*-
'''
Created on 2018年5月1日

文章地域分类系统

@author: Gdx
'''
import sys
import jieba
import traceback
from sklearn.externals import joblib
from scipy.sparse import csr_matrix
reload(sys)
sys.setdefaultencoding('utf8')

file_path = sys.path[1] + '/file/'
jieba.load_userdict(file_path + 'jieba_usr_file.txt')

class Loc_Classify:
    ALL_FEA_LIST = []  
    ALL_PATH_MAP = {}
    
    
    def __init__(self):
        print '正在初始化模型......'
        self.load_fea_list()
        self.load_ALL_PATH_MAP()  
        print '初始化完毕！'

    def load_ALL_PATH_MAP(self):
        try:
            all_path_file = open(r'D:\data\GraduationDesign\poi_file\all_path')
            for line in all_path_file.readlines():
                line = line.strip()
                if len(line) > 0 :
                    tmp_list = line.split('\t')
                    Loc_Classify.ALL_PATH_MAP[tmp_list[0]] = tmp_list[1:4]
        except:
            print 'read all path error: %s'
            print 'traceback.print_exc():'; traceback.print_exc()
#         print 'all_path_map size: %d' % (len(Loc_Classify.ALL_PATH_MAP))

    def load_fea_list(self):
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
                    Loc_Classify.ALL_FEA_LIST.append(line)
                line_count += 1
        except:
            print 'Error in load_fea_list'
#         print 'all_feature_list size: %d' % (len(Loc_Classify.ALL_FEA_LIST))

    def search_main_name(self, test_name):
        """
        函数功能： 
            通过读取redis数据库，返回待查询地点的主名
        参数：
            test_name: 待查询地点的String
        返回：
            main_name: 待查询地点对应的主名
        """
        main_name = ''

        test_name = test_name.strip()
        try:
            if test_name in Loc_Classify.ALL_PATH_MAP:
                redis_list = Loc_Classify.ALL_PATH_MAP[test_name]
                if len(redis_list) > 0:
                    if redis_list[2] == 'nick':
                        # if len(redis_list[1].split(', ')) > 0:
                        to_test_main_list = []
                        if ', ' in redis_list[1]:
                            to_test_main_list = redis_list[1].split(', ')
                        else:
                            to_test_main_list = [redis_list[1].strip()]
                        for to_test_main in to_test_main_list:
                            if len(to_test_main) > 0 and to_test_main in Loc_Classify.ALL_PATH_MAP:
                                to_test_main_tmp_list = Loc_Classify.ALL_PATH_MAP[to_test_main]
                                if len(to_test_main_tmp_list) > 0 and to_test_main_tmp_list[2] == 'main':
                                    main_name = to_test_main
                                    break        
                    elif redis_list[2] == 'main':
                        main_name = test_name.strip()
        except:
            print 'traceback.print_exc():'; traceback.print_exc()
            print 'Error in search_main_name '
            return ''
        return main_name

    def search_all_name(self, test_name):
        """
        函数功能： 
        通过读取redis数据库，返回待查询地点的所有主别名
        参数：
            test_name: 待查询地点的String
        返回：
            all_name_list: 待查询地点对应的主名列表
        """
        all_name_list = []
        test_name = test_name.strip()
        try:
            if test_name in Loc_Classify.ALL_PATH_MAP:
                redis_list = Loc_Classify.ALL_PATH_MAP[test_name]
                if len(redis_list) > 0:
                    all_name_list = [test_name]
                    if len(redis_list[1]) > 0 and len(redis_list[1].split(', ')) > 0:
                        all_name_list = all_name_list + redis_list[1].split(', ')
        except:
            print 'Error in search_all_name '
            return []
        return all_name_list
    
    def search_upclass(self, test_name):
        """
        函数功能：
            通过读取redis数据库，返回待查询地点所在路径的主名
        参数:
            test_name： 查询地点的String
        返回：
            upclass_name_list: 待查询地点所在路径上级（包括本身）的主名
        """
        upclass_name_list = []
        test_name = test_name.strip()
        try:
            if test_name in Loc_Classify.ALL_PATH_MAP and len(Loc_Classify.ALL_PATH_MAP[test_name]) > 0:
                redis_list = Loc_Classify.ALL_PATH_MAP[test_name]
                path_string = redis_list[0]
                if len(path_string) > 0:
                    path_string_list = []
                    if ',' in path_string:
                        path_string_list = path_string.split(',')
                    else:
                        path_string_list = [path_string]
                    for i in range(len(path_string_list)):  # 进行去除前后空格
                        path_string_list[i] = path_string_list[i].strip()
                    # 遍历每条路径
                    for path_item in path_string_list:
                        if '->' in path_item:
                            upclass_name_list = upclass_name_list + path_item.split('->')[:-1]

            upclass_name_list = list(set(upclass_name_list))
            main_name = self.search_main_name(test_name)
            if len(main_name) > 0 and main_name not in upclass_name_list:
                upclass_name_list.insert(0, main_name)
        except:
            print 'Error in search_upclass'
            upclass_name_list = []
    
        return upclass_name_list
    
    def search_path(self, test_name):
        """
        函数功能：
            通过读取redis数据库，返回待查询地点所在路径的主名
        参数： 
            test_name
        返回：
            所在路径eg: 广东省->广州市->白云区
        """
        path_list = []
        try:
            if test_name in Loc_Classify.ALL_PATH_MAP and len(Loc_Classify.ALL_PATH_MAP[test_name]) > 0:
                redis_list = Loc_Classify.ALL_PATH_MAP[test_name]
                if len(redis_list[0]) > 0:
                    if ',' in redis_list[0]:
                        path_list = path_list + redis_list[0].split(',')
                    else:
                        path_list.append(redis_list[0])
            if len(path_list) > 0:
                for index in range(len(path_list)):
                    path_list[index] = path_list[index].strip()
        except:
            print 'Error in search_path'
            path_list = []
        return path_list
                            


    def parse_title_or_content(self, ID, fea_weight_map, split_str, str_type, content):
        """
        函数功能：
            解析title和content，进行向量化表示
        参数：
            ID: 文章ID
            fea_weight_map：当前的文本向量map
            split_str：待解析的字符串
            str_type：split_str的类型，是title还是content
            content: 文章的标题+内容，未分词
        返回：
            fea_weight_map: 解析了该str后的fea_weight_map
        """
#         print '【%s】进入函数: parse_title_or_content, str_type: %s' % (ID, str_type)
        try:
            fea_weight = 0.0
            if str_type == 'title':
                fea_weight = 5.0
                
            elif str_type == 'content':
                fea_weight = 1.0           
                        
            elif str_type == 'source':
                fea_weight = 2.0    
                
            # 解析字符串
            split_str_list = split_str.split(' ')
            
            # 将source的split_list变得唯一
            if str_type == 'source':
                split_str_list2 = []
                for unique_index in range(len(split_str_list)):
                    word_type = split_str_list[unique_index]
                    if len(word_type) >= 2 and word_type not in  split_str_list2:
                        split_str_list2.append(word_type)
                del split_str_list
                split_str_list = split_str_list2    
                # print 'source list: %s' % str(split_str_list).decode('string_escape')        
                
                
            # 遍历切分后的每个word_type对
            for word_index in range(len(split_str_list)):
                word_type = split_str_list[word_index].strip()
                word = ''
                # 进行词语清洗
                if '_' in word_type and len(word_type) > 3:
                    try:
                        word = word_type.split('_')[0]
                        word = word.strip()
                    except:
                        print '【%s】error in parse_title_or_content: 解析word_type%s对时，出现bug!' % (ID, str(word_type))
                        continue
                else:
                    word = word_type
                # print word + '; ',
                if len(word) <= 1 or word == ' ':
                    continue

                if word == '北京' or word == '上海' or word == '天津' or word == '重庆':
                    word += '市'
                if word == '河北' or word == '山西' or word == '辽宁' or word == '吉林' or word == '黑龙江' or word == '江苏' or word == '浙江' or word == '安徽' or word == '福建' or word == '江西' or word == '山东' or word == '河南' or word == '湖北' or word == '湖南' or word == '广东' or word == '海南' or word == '四川' or word == '贵州' or word == '云南' or word == '陕西' or word == '甘肃' or word == '青海' or word == '台湾' :
                    word += '省'
                if word == '内蒙古' or word == '广西壮族' or word == '西藏' or word == '宁夏回族' or word == '新疆维吾尔' :
                    word += '自治区'
                if word == '香港' or word == '澳门' :
                    word += '特别行政区'
                
                # 地名+人 -> 地名
                if len(word) >= 2 and word.decode('utf-8')[-1].encode('utf-8') == '人' and word.decode('utf-8')[0:-1].encode('utf-8') in Loc_Classify.ALL_FEA_LIST:
                    try:
                        word = word.decode('utf-8')[0:-1].encode('utf-8')
                    except:
                        print '【%s】error in parse_title_or_content: 这个词含有关键字_人' % (ID, word)
                        continue
        
                # 进行权重整理
                if len(word) > 1 and word in Loc_Classify.ALL_FEA_LIST: 
                    try:
                        # 抽取其中的关键词,更新fea_weight_map
                        index = Loc_Classify.ALL_FEA_LIST.index(word)
                        
                        if fea_weight_map.has_key(index) == False:
                            fea_weight_map[index] = fea_weight
                        else:
                            origin_value_in_fea_vec = fea_weight_map[index]
                            fea_weight_map[index] = fea_weight + origin_value_in_fea_vec

                    except:
                        print 'traceback.print_exc():'; traceback.print_exc()
                        print '【%s】error parse_title_or_content: 对ALL_FEA_LIST中的词解析错误' % ID
                        continue
                        
        except:
            print 'traceback.print_exc():'; traceback.print_exc()
            print '【%s】error parse_title_or_content' % ID
            fea_weight_map = {}
        return fea_weight_map
    
    def predict_by_article(self, ID, title, source, content):
        fea_weight_map = {}
        print 'ID: ' + ID
        
        print '文本转化为向量开始...'
        split_title = ' '.join(jieba.cut(title))
        fea_weight_map = self.parse_title_or_content(ID, fea_weight_map, split_title, 'title', content)
        split_source = ' '.join(jieba.cut(source))
        fea_weight_map = self.parse_title_or_content(ID, fea_weight_map, split_source, 'source', content)
        split_content = ' '.join(jieba.cut(content))
        fea_weight_map = self.parse_title_or_content(ID, fea_weight_map, split_content, 'content', content)
        
#         print split_title
#         print split_source
#         print split_content
#         print "地名-权重："+str(fea_weight_map).decode('string_escape')       
            
        row = []
        col = []
        data = []
        
        for tu in fea_weight_map.items():
            k = int(tu[0])
            if k <= 0:
                fea_weight_map.pop(tu[0])
            else:
                col.append(tu[0])
                data.append(int(tu[1] * 100))
                row.append(0)
            
        input_array = csr_matrix((data, (row, col)), shape=(1, 10015)).toarray()
        print '文本转化为向量完毕！' 
        
        # 预测结果
        tree_number = 90
        model_file_path = r"D:\data\GraduationDesign\Model"
        this_model_path = model_file_path + r"\model_" + str(tree_number) + "_none.pkl"
#         clf = RandomForestClassifier(n_estimators=70, n_jobs=-1)
        print '加载随机森林模型......'
        clf = joblib.load(this_model_path)
        print '加载完毕！'
        pre_result = clf.predict(input_array)
#         print "预测结果" + str(pre_result).decode('string_escape')
        
        result = self.ALL_FEA_LIST[pre_result[0]]
        return result
    
        



