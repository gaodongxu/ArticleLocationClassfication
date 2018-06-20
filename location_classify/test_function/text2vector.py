# -*- coding: utf-8 -*-
import sys
import jieba
import re
import traceback
reload(sys)
sys.setdefaultencoding('utf8')

file_path = sys.path[1] + '/file/'
jieba.load_userdict(file_path + 'jieba_usr_file.txt')

class Loc_recognize_class:
    LOC_List = []
    TRAVEL_PLACE_LIST = []
    TAG_SOURCE_LIST = []
    ALL_FEA_LIST = []
    SOURCE = []
    KEYWORDS = []    
    FOREIGN_LOC_LIST = []
    ECONOMY_LIST = []
    UNIFIED_PROVINCE_AND_CITY_LIST = []    
    REDIS_MAP = {}
    
    
    def __init__(self):
        print '正在初始化模型......'
        self.load_fea_list()
        self.load_unified_province_and_city_list()  
        self.load_redis_map()  
        print '初始化完毕！'
       
    def load_unified_province_and_city_list(self):
        try:
            with open(file_path + 'unified_loc_name_map.txt', 'r') as reader:
                line = reader.readline()
                line = line.strip()
                dict_tmp = eval(line)
                province_list = dict_tmp["province"].values()
                city_list = dict_tmp["city"].values()    
                self.UNIFIED_PROVINCE_AND_CITY_LIST = province_list + city_list
            print 'UNIFIED_PROVINCE_AND_CITY_LIST size: %d' % len(self.UNIFIED_PROVINCE_AND_CITY_LIST)        
        except Exception, e:
            print str(Exception) + ':' + str(e)


    def load_redis_map(self):
        try:
            all_path_file = open(r'D:\data\GraduationDesign\all_path')
            for line in all_path_file.readlines():
                line = line.strip()
                if len(line) > 0 :
                    tmp_list = line.split('\t')
                    Loc_recognize_class.REDIS_MAP[tmp_list[0]]=tmp_list[1:4]
        except:
            print 'read redis error: %s'
        print 'redis size: %d' % (len(Loc_recognize_class.REDIS_MAP))

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
                    Loc_recognize_class.ALL_FEA_LIST.append(line)
                line_count += 1

            Loc_recognize_class.LOC_List = Loc_recognize_class.ALL_FEA_LIST[:6430]
            Loc_recognize_class.TRAVEL_PLACE_LIST = Loc_recognize_class.ALL_FEA_LIST[6430:]
            Loc_recognize_class.SOURCE = Loc_recognize_class.ALL_FEA_LIST[11055:32068]
            Loc_recognize_class.KEYWORDS = Loc_recognize_class.ALL_FEA_LIST[32068:]    
            # Loc_recognize_class.TAG_SOURCE_LIST = Loc_recognize_class.ALL_FEA_LIST[7479:]
        except:
            print 'Error in load_fea_list'


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
            if test_name in Loc_recognize_class.REDIS_MAP:
                redis_list = Loc_recognize_class.REDIS_MAP[test_name]
                if len(redis_list) > 0:
                    if redis_list[2] == 'nick':
                        # if len(redis_list[1].split(', ')) > 0:
                        to_test_main_list = []
                        if ', ' in redis_list[1]:
                            to_test_main_list = redis_list[1].split(', ')
                        else:
                            to_test_main_list = [redis_list[1].strip()]
                        for to_test_main in to_test_main_list:
                            if len(to_test_main) > 0 and to_test_main in Loc_recognize_class.REDIS_MAP:
                                to_test_main_tmp_list = Loc_recognize_class.REDIS_MAP[to_test_main]
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
            if test_name in Loc_recognize_class.REDIS_MAP:
                redis_list = Loc_recognize_class.REDIS_MAP[test_name]
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
            if test_name in Loc_recognize_class.REDIS_MAP and len(Loc_recognize_class.REDIS_MAP[test_name]) > 0:
                redis_list = Loc_recognize_class.REDIS_MAP[test_name]
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
            if test_name in Loc_recognize_class.REDIS_MAP and len(Loc_recognize_class.REDIS_MAP[test_name]) > 0:
                redis_list = Loc_recognize_class.REDIS_MAP[test_name]
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
                            


    def parse_title_or_content(self, ID, fea_weight_map, split_str, str_type, is_conversation, is_need_focus, title_contain_local_info, content):
        """
        函数功能：
            解析title和content，进行向量化表示
        参数：
            ID: 文章ID
            fea_weight_map：当前的文本向量map
            split_str：待解析的字符串
            str_type：split_str的类型，是title还是content
            is_conversation: 是否为访谈类节目
            is_need_focus: 判断文章是否为“XX请注意”类文章
            title_contain_local_info: 标题中是否包含了"市政府"等字样
            content: 文章的标题+内容，未分词
        返回：
            fea_weight_map: 解析了该str后的fea_weight_map
        """
        print '【%s】进入函数: parse_title_or_content, str_type: %s' % (ID, str_type)
        try:
        # if True:    
            # 根据str_type的不同，决定权重增加的多少
            fea_weight = 0.0
            pre_loc_weight = 0
            if str_type == 'title':
                fea_weight = 5.0
                pre_loc_weight = 2.9
                if is_need_focus == True:
                    pre_loc_weight = 5            
                if '省委' in split_str or '省政府' in split_str or '市政府' in split_str or '市委' in split_str or '督查组' in split_str:
                    pre_loc_weight = pre_loc_weight * 2
                    title_contain_local_info = True

        
            elif str_type == 'content':
                fea_weight = 1.0
                pre_loc_weight = 1    
                if title_contain_local_info == True:
                    pre_loc_weight = pre_loc_weight * 2.0
                else:
                    if '省委' in split_str or '省政府' in split_str or '市政府' in split_str or '市委' in split_str or '督查组' in split_str:
                        pre_loc_weight = pre_loc_weight * 1.3
                
                        
                        
            elif str_type == 'source':
                fea_weight = 2.0
                pre_loc_weight = 2.9 
                if title_contain_local_info == True:
                    pre_loc_weight = pre_loc_weight * 2.0
                else:
                    if '省委' in split_str or '省政府' in split_str or '市政府' in split_str or '市委' in split_str or '督查组' in split_str:
                        pre_loc_weight = pre_loc_weight * 1.3
            
            
            if is_conversation == True:
                pre_loc_weight = pre_loc_weight * 0.5

            # log.info('【%s】parse_title_or_content: 类型：%s；is_need_focus: %s; title_contain_local_info:%s；权重pre_loc_weight：%f； 权重fea_weight：%f'%(ID,str_type,str(is_need_focus), str(title_contain_local_info), pre_loc_weight, fea_weight))
                
            # 解析字符串
            split_str_list = split_str.split(' ')
            
            # 将source的split_list变得唯一
            if str_type == 'source':
                split_str_list2 = []
                for unique_index in range(len(split_str_list)):
                    word_str_type = split_str_list[unique_index]
                    if len(word_str_type) >= 2 and word_str_type not in  split_str_list2:
                        split_str_list2.append(word_str_type)
                del split_str_list
                split_str_list = split_str_list2    
                # print 'source list: %s' % str(split_str_list).decode('string_escape')        
                
                
            # 遍历切分后的每个word_str_type对
            for word_index in range(len(split_str_list)):
                word_str_type = split_str_list[word_index].strip()
                word = ''
                # 进行词语清洗
                if '_' in word_str_type and len(word_str_type) > 3:
                    try:
                        word = word_str_type.split('_')[0]
                        word = word.strip()
                    except:
                        print '【%s】error in parse_title_or_content: 解析word_str_type%s对时，出现bug!' % (ID, str(word_str_type))
                        continue
                else:
                    word = word_str_type
                # print word + '; ',
                if len(word) <= 1 or word == ' ':
                    continue

                # 地名+人 -> 地名
                if len(word) >= 2 and word.decode('utf-8')[-1].encode('utf-8') == '人' and word.decode('utf-8')[0:-1].encode('utf-8') in Loc_recognize_class.ALL_FEA_LIST:
                    try:
                        word = word.decode('utf-8')[0:-1].encode('utf-8')
                    except:
                        print '【%s】error in parse_title_or_content: 这个词含有关键字_人' % (ID, word)
                        continue
                
    
                # 计算裸的地点词语和上下文的聚合度
                try:
                    if len(word) >= 2 and word in Loc_recognize_class.LOC_List and len(self.search_all_name(word)) > 0 and not re.search('省|市|县|区|乡|镇|村|州|盟|旗' , word) and str_type == 'content':
                        
                        # 获取窗口=1的前一个词
                        if word_index > 0:
                            previous_word_conbination = split_str_list[word_index - 1].strip()
                            previous_word = ''
                            # 进行词语清洗
                            try:
                                if '_' in previous_word_conbination:
                                    last_index = previous_word_conbination.rfind('_')
                                    # item_list = item.split('_')
                                    previous_word = str(previous_word_conbination[:last_index]).decode('string_escape').strip()
                                else:
                                    previous_word = previous_word_conbination.strip()
                            except:
                                previous_word = ''
                                print '【%s】error in parse_title_or_content: 解析word_str_type%s对时，出现bug!' % (ID, str(word_str_type))
                                
                            # 判断前文不是标点符号, 以及 是否满足结合度
                            if '' != previous_word and len(previous_word) > 0: 
                                matchObj = re.match('[’!"#$%&\'()*+,-./:;<=>?@“[\\]^_`{|}~，。、！？【】…]+' , previous_word)
                                if not matchObj:
                                    # 正文出现的次数
                                    if (previous_word + word) in Loc_recognize_class.LOC_List:
                                        word = previous_word + word
                                    else:
                                        if not re.search('省|市|县|区|乡|镇|村|州|盟|旗' , word):
                                            if content.count((previous_word + word)) > content.count(word) * 0.8 and content.count(word) >= 2:
                                                print '【%s】解决分词问题，与前词搭配，认定为有效词组%s' % (ID, (previous_word + word))
                                                word = ''
                                        
                                    
                        #     获取窗口=1的后一个词
                        if word_index < len(split_str_list) - 1:
                            next_word_conbination = split_str_list[word_index + 1].strip()
                            next_word = ''
                            # 进行词语清洗
                            try:
                                if '_' in next_word_conbination:
                                    last_index = next_word_conbination.rfind('_')
                                    # item_list = item.split('_')
                                    next_word = str(next_word_conbination[:last_index]).decode('string_escape').strip()
                                else:
                                    next_word = next_word_conbination.strip()
                            except:
                                next_word = ''
                                print '【%s】error in parse_title_or_content: 解析word_str_type%s对时，出现bug!' % (ID, str(word_str_type))
                                continue
                                
                            # 判断后文不是标点符号, 以及 是否满足结合度
                            if '' != next_word and len(next_word) > 0: 
                                matchObj = re.match('[’!"#$%&\'()*+,-./:;<=>?@“[\\]^_`{|}~，。、！？【】…]+' , next_word)
                                if not matchObj:
                                    if (word + next_word) in Loc_recognize_class.LOC_List:
                                        word = (word + next_word)
                                    else:
                                        if not re.search('省|市|县|区|乡|镇|村|州|盟|旗' , word):
                                            if content.count((word + next_word)) > content.count(word) * 0.8 and content.count(word) >= 2:
                                                print '【%s】解决分词问题，与后词搭配，认定为有效词组%s' % (ID, (word + next_word))
                                                word = ''
                except:
                    print 'traceback.print_exc():'; traceback.print_exc()
                    print '【%s】在计算词语结合度时，发生异常 %s' % (ID, word)
                    continue
                    
        
                # 进行权重整理
                if len(word) > 1 and word in Loc_recognize_class.ALL_FEA_LIST: 
                    try:
                        # 抽取其中的关键词,更新fea_weight_map
                        index = Loc_recognize_class.ALL_FEA_LIST.index(word)
                        
                        if fea_weight_map.has_key(index) == False:
                            fea_weight_map[index] = fea_weight
                        else:
                            origin_value_in_fea_vec = fea_weight_map[index]
                            fea_weight_map[index] = fea_weight + origin_value_in_fea_vec

                        # 如果是地域特征词，更新pre_loc_map
                        if word in Loc_recognize_class.LOC_List and len(self.search_all_name(word)) > 0:
                            try:
                                # 判断该地域词后面的7个窗口有木有'公司'或者'有限公司'的字样
                                next_windows = 1
                                is_company_name = False
                                while next_windows <= 7 and (word_index + next_windows) < len(split_str_list):
                                    word_within_windows = split_str_list[word_index + next_windows].strip()
                                    if '公司' in word_within_windows or '有限公司' in word_within_windows:
                                        is_company_name = True
                                        print '【%s】出现公司名称：%s' % (ID, str(''.join(split_str_list[word_index: word_index + next_windows + 1 ])).decode('string_escape'))
                                        break
                                    next_windows += 1    
                                if is_company_name == True:
                                    continue
                            except:
                                print '【%s】error in parse_title_or_content: 判断是否为公司名称' % ID
                            this_word_upclass_list = self.search_upclass(word)
                            if len(this_word_upclass_list) <= 0:
                                continue

                    except:
                        print 'traceback.print_exc():'; traceback.print_exc()
                        print '【%s】error parse_title_or_content: 对ALL_FEA_LIST中的词解析错误' % ID
                        continue
                        
        except:
            print 'traceback.print_exc():'; traceback.print_exc()
            print '【%s】error parse_title_or_content' % ID
            fea_weight_map = {}
        return fea_weight_map, title_contain_local_info
    

if __name__ == '__main__':
    
    ID = '231231231'
    title = '市政府安委会第四督查组来叶集区督查夏季暨汛期安全生产工作'
    content = '''
    8月14日，六安市交通运输局副局长江善林率市政府安委会第四督查组来叶集区督查夏季暨汛期安全生产工作。区委常委、副区长韦能武陪同。
督查组一行首先听取了区政府关于夏季暨汛期安全生产大检查大督查工作的汇报，查阅了相关台账资料。随后，深入丽人木业、南方水泥、振宇建设等企业，实地查看了生产现场及安全资料。随行的安全专家对企业进行了全面 “体检”，为企业更好的安全发展开具了“药方”。
督查组一行对叶集区夏季暨汛期安全生产大检查大督查工作取得的成效给予充分肯定。认为该区高度重视安全生产工作，责任落实到位，狠抓薄弱环节，行动开展到位。下一步要继续扎实开展“百日除患铸安”专项行动，加大隐患排除力度，全力做好安全生产工作，为党的十九大胜利召开营造稳定的安全生产环境。
'''
    source = '六安新闻网'
    tag_list = []

    loc_obj = Loc_recognize_class()
    print str(loc_obj.UNIFIED_PROVINCE_AND_CITY_LIST).decode('string_escape')
    # print str(loc_obj.ALL_FEA_LIST).decode('string_escape')
    
    fea_weight_map = {}
        
    title_contain_local_info = False
    is_conversation = False
    is_need_focus = False
    
    split_title = ' '.join(jieba.cut(title))
    (fea_weight_map, title_contain_local_info) = loc_obj.parse_title_or_content(ID, fea_weight_map, split_title, 'title', is_conversation, is_need_focus, title_contain_local_info, content)
    split_source=' '.join(jieba.cut(source))
    (fea_weight_map, title_contain_local_info) = loc_obj.parse_title_or_content(ID, fea_weight_map, split_source, 'source', is_conversation, is_need_focus, title_contain_local_info, content)
    split_content=' '.join(jieba.cut(content))
    (fea_weight_map, title_contain_local_info) = loc_obj.parse_title_or_content(ID, fea_weight_map, split_content, 'content', is_conversation, is_need_focus, title_contain_local_info, content)
    
    
#     print len(loc_obj.TRAVEL_PLACE_LIST)
    print split_title
    print str(fea_weight_map).decode('string_escape')
    print str(title_contain_local_info).decode('string_escape')
            
         
       
