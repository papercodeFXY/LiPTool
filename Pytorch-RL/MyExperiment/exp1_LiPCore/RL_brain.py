#coding:utf-8
import os

import torch
import torch.nn as nn
import torch.nn.functional as F
import numpy as np
import gym
import pandas as pd
import random
import datetime
import matplotlib.pyplot as plt
import pylab as pl
from itertools import chain
import time
import sys
if sys.version_info.major == 2:
    import Tkinter as tk
else:
    import tkinter as tk

from flask import Flask,request
# from flask import Flask
from flask_cors import CORS, cross_origin




app = Flask(__name__)
CORS(app, supports_credentials=True)

@app.route("/workload_file_list/", methods=['GET', 'POST'])
def file_list():
    list = ["QueryAttribute_longtail_50",
            "QueryAttribute_longtail_100",
            "QueryAttribute_longtail_200",
            "QueryAttribute_longtail_300",
            "QueryAttribute_longtail_400",
            "QueryAttribute_poisson_50",
            "QueryAttribute_poisson_100",
            "QueryAttribute_poisson_200",
            "QueryAttribute_poisson_300",
            "QueryAttribute_poisson_400",
            "QueryAttribute_longtail_500",
            "QueryAttribute_poisson_500"]
    print("Already called")
    return list


class Cluster(tk.Tk, object):
    def __init__(self, state_init, server_attribute, QSs, server_number):
        super(Cluster, self).__init__()
        self.server_number = server_number
        self.cost_matrix = pd.DataFrame(np.array([[0,1,5,12,7,10,15,9],
                                                  [1,0,4,2,8,6,11,10],
                                                  [5,4,0,3,11,13,8,5],
                                                  [12,2,3,0,7,6,10,4],
                                                  [7,8,11,7,0,12,9,5],
                                                  [10,6,13,6,12,0,3,8],
                                                  [15,11,8,10,9,3,0,10],
                                                  [9,10,5,4,5,8,10,0]]),
                                        columns=[0, 1, 2, 3, 4, 5, 6, 7])
        self.server_attribute = server_attribute
        self.QSs = QSs
        self.action_space = np.array(self.action_list())
        self.n_actions = len(self.action_space)
        self.state_init = state_init

        self.cost_init = self.cost_init()


    def step(self, action, state, costs):
        s = state.copy()
        #action_real[查询，移动到的服务器]
        if isinstance(action, np.ndarray):
            action = action[0]
        # print(action)
        action_real = self.action_space[action]
        # print(action_real)
        q = action_real[0]
        index_server = action_real[1]
        s.iloc[q, :] = 0
        s.iloc[q, index_server] = 1

        cost_new = self.cost_caculate(q, index_server)

        costs[q] = cost_new
        cost_all = self.cost_all(costs)
        reward = self.reward(cost_all, s)
        s_ = s
        return s_, costs, reward, cost_all, action



    # generate the total action set
    def action_list(self):
        action = []
        for i in range(len(self.QSs)):
            for j in range(self.server_number):
                li = []
                li.append(i)
                li.append(j)
                action.append(li)
        return action


    # compute the initial costs array based on the initial state matrix. every element represent the total cost of the query
    def cost_init(self):
        state_init = self.state_init
        # print(len(state_init))
        states = self.state_array(state_init)
        # print(len(states))
        costs = []
        # print(len(state_init))
        for i in range(len(state_init)):
            index_server = states[i][1]
            cost = self.cost_caculate(i,  index_server)
            costs.append(cost)
        return costs


    def cost_caculate(self,q,index_server):
        cost = 0
        for j in range(len(self.QSs[q][1])):
            target_server = self.QSs[q][1][j]
            cost += self.cost_matrix.iloc[index_server, target_server]
        return cost


    # compute the total reward based on the costs array
    def cost_all(self, costs):
        cost_all = 0
        for i in range(len(costs)):
            cost_all += costs[i]
        return cost_all

    def reward(self, cost_all, state):
        list = []
        for i in state.columns:
            list.append(state[i].sum())

        load_weight_var = np.var(list)
        reward = 100*self.tanh((10*len(state)/cost_all))*self.tanh(10*self.function(1.1, load_weight_var))
        # reward = 100*10*(len(state)/cost_all)*self.tanh(100*self.function(1.1, load_weight_var))
        # reward = (len(state)/cost_all) * 100 * self.function(1.1, load_weight_var)
        return reward

    def reward_local(self, cost_all, state):
        list_var = []
        for i in state.columns:
            list_var.append(state[i].sum())
        load_weight_var = np.var(list_var)
        # reward = (len(state)/cost_all) * 100 * self.function(1.1, load_weight_var)
        # reward = 100*10*(len(state)/cost_all)*self.tanh(100*self.function(1.1, load_weight_var))
        # reward = (len(state)/cost_all) * 100*self.function(1.1, load_weight_var)
        reward = 100*self.tanh((10*len(state)/cost_all))*self.tanh(10*self.function(1.1, load_weight_var))
        print("cost", 100*10*(len(state)/cost_all))
        return reward, list_var, load_weight_var

    def function(self, a, x):
        y = 1/(a**x)
        return y

    def tanh(self, x):
        return (np.exp(x) - np.exp(-x)) / (np.exp(x) + np.exp(-x))

    # transform the state matrix into array
    def state_array(self, state):
        states = []
        for i in range(len(state)):
            for j in range(len(state.columns)):
                state_arr = []
                if state.iloc[i, j] == 1:
                    state_arr.append(i)
                    state_arr.append(j)
                    states.append(state_arr)
        return states


DataSet_filePath = "MyExperiment\exp1_LiPCore\data\QueryAttribute_longtail_600"
server_number = 8
server_attribute = pd.DataFrame(np.array([0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                          0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
                                          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0,
                                          1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0,
                                          0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                                          0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0,
                                          0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
                                          0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0]).
                                reshape(8, 24),
                                columns=np.arange(24))

def read_file(DataSet_filePath):
    with open(DataSet_filePath, 'r') as f:
        content = f.readlines()
        QSs = []
        for item in content:
            QS = []
            item = item.strip("\n")
            q = item.split(",")[0]
            targetAttribute = item.split(",")[1:]
            targetAttribute = list(map(int, targetAttribute))
            servers = []
            for attribute in targetAttribute:
                server = server_attribute[server_attribute.loc[:, attribute] == 1].index[0]
                servers.append(server)
            QS.append(int(q))
            QS.append(servers)
            QSs.append(QS)
    return QSs

def state_init(DataSet_filePath):
    init_state = pd.DataFrame(np.zeros(len(read_file(DataSet_filePath))*server_number).reshape(len(read_file(DataSet_filePath)), server_number), columns=np.arange(server_number))
    for i in range(len(init_state)):
        j = random.randint(0, len(init_state.columns)-1)
        init_state.iloc[i][j] = 1
    return init_state

# Hyper Parameters
BATCH_SIZE = 256
LR = 0.0005                   # learning rate
EPSILON = 0.8               # greedy policy
GAMMA = 0.9                 # reward discount
TARGET_REPLACE_ITER = 5    # target update frequency
MEMORY_CAPACITY = 10000
# server_attribute = pd.DataFrame(np.array([0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0,
#                                           0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0,
#                                           1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0,
#                                           0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1]).
#                                 reshape(4, 12),
#                                 columns=np.arange(12))
init_state = state_init(DataSet_filePath)
QSs = read_file(DataSet_filePath)
env = Cluster(init_state, server_attribute, QSs, server_number)
# N_ACTIONS = len(env.action_space)
# N_STATES = len(env.state_init)*len(env.state_init.columns)
# ENV_A_SHAPE = 0 if isinstance(env.action_space.sample(), int) else env.action_space.sample().shape
# device = torch.device('cuda:0'if torch.cuda.is_available() else "cpu")

class Net(nn.Module):
    def __init__(self, N_STATES, N_ACTIONS):
        super(Net, self).__init__()
        # self.fc1 = nn.Linear(N_STATES, 50)
        self.fc1 = nn.Linear(N_STATES, 100)
        self.fc1.weight.data.normal_(0, 0.1)   # initialization
        self.fc2 = nn.Linear(100, 100)
        self.fc2.weight.data.normal_(0, 0.1)
        #self.out = nn.Linear(50, N_ACTIONS)
        self.out = nn.Linear(100, N_ACTIONS)
        self.out.weight.data.normal_(0, 0.1)   # initialization

    def forward(self, x):
        x = self.fc1(x)
        x = F.relu(x)
        x = self.fc2(x)
        x = F.relu(x)
        actions_value = self.out(x)
        return actions_value


class DQN(object):
    def __init__(self, N_STATES, N_ACTIONS, device):
        self.N_STATES = N_STATES
        self.N_ACTIONS = N_ACTIONS
        self.device = device
        self.eval_net, self.target_net = Net(self.N_STATES, self.N_ACTIONS).to(self.device), Net(self.N_STATES,self.N_ACTIONS).to(self.device)
        self.learn_step_counter = 0                                     # for target updating
        self.memory_counter = 0                                         # for storing memory
        self.memory = np.zeros((MEMORY_CAPACITY, N_STATES * 2 + 2))     # initialize memory
        self.optimizer = torch.optim.Adam(self.eval_net.parameters(), lr=LR)
        self.loss_func = nn.MSELoss()

    def choose_action(self, x):
        x = torch.unsqueeze(torch.FloatTensor(x), 0).to(self.device)
        # input only one sample
        if np.random.uniform() < EPSILON:   # greedy
            actions_value = self.eval_net.forward(x)
            action = torch.max(actions_value, 1)[1].data.cpu().numpy()
            is_random = False
            # action = action[0] if ENV_A_SHAPE == 0 else action.reshape(ENV_A_SHAPE)  # return the argmax index
        else:   # random
            action = np.random.randint(0, self.N_ACTIONS)
            is_random = True
            # action = action if ENV_A_SHAPE == 0 else action.reshape(ENV_A_SHAPE)
        return action, is_random

    def store_transition(self, s, a, r, s_):
        transition = np.hstack((s, [a, r], s_))
        # replace the old memory with new memory
        index = self.memory_counter % MEMORY_CAPACITY
        self.memory[index, :] = transition
        self.memory_counter += 1

    def learn(self):
        # target parameter update
        if self.learn_step_counter % TARGET_REPLACE_ITER == 0:
            self.target_net.load_state_dict(self.eval_net.state_dict())
            self.target_net.to(self.device)
            # print(next(self.target_net.parameters()).device)
        self.learn_step_counter += 1

        # sample batch transitions
        sample_index = np.random.choice(MEMORY_CAPACITY, BATCH_SIZE)
        b_memory = self.memory[sample_index, :]
        b_s = torch.FloatTensor(b_memory[:, :self.N_STATES]).to(self.device)
        b_a = torch.LongTensor(b_memory[:, self.N_STATES:self.N_STATES+1].astype(int)).to(self.device)
        b_r = torch.FloatTensor(b_memory[:, self.N_STATES+1:self.N_STATES+2]).to(self.device)
        b_s_ = torch.FloatTensor(b_memory[:, -self.N_STATES:]).to(self.device)

        # q_eval w.r.t the action in experience
        q_eval = self.eval_net(b_s).gather(1, b_a)  # shape (batch, 1)
        q_next = self.target_net(b_s_).detach()     # detach from graph, don't backpropagate
        q_target = b_r + GAMMA * q_next.max(1)[0].view(BATCH_SIZE, 1)   # shape (batch, 1)
        loss = self.loss_func(q_eval, q_target)
        # print("loss:", loss)

        self.optimizer.zero_grad()
        loss.backward()
        self.optimizer.step()


def return_img_stream(img_local_path):
    """
    工具函数:
    获取本地图片流
    :param img_local_path:文件单张图片的本地绝对路径
    :return: 图片流
    """
    import base64
    img_stream = ''
    with open(img_local_path, 'rb') as img_f:
        img_stream = img_f.read()
        img_stream = base64.b64encode(img_stream).decode()
    return img_stream


@app.route("/get_train_result/", methods=['GET', "POST"])
def get_train_result():
    # DataSet_filePath = "QueryAttribute_poisson_50"
    DataSet_filePath = 'data/' + request.values.get("filename")
    print(DataSet_filePath)
    init_state = state_init(DataSet_filePath)
    QSs = read_file(DataSet_filePath)
    env = Cluster(init_state, server_attribute, QSs, server_number)
    N_ACTIONS = len(env.action_space)
    N_STATES = len(env.state_init)*len(env.state_init.columns)
    # ENV_A_SHAPE = 0 if isinstance(env.action_space.sample(), int) else env.action_space.sample().shape
    device = torch.device('cuda:0'if torch.cuda.is_available() else "cpu")
    curr_time1 = datetime.datetime.now()
    dqn = DQN(N_STATES, N_ACTIONS, device)
    print('\nCollecting experience...')
    cost_all_list = []
    reward_all_list = []
    init_reward = env.reward(env.cost_all(env.cost_init), env.state_init)
    for i_episode in range(60000):
        epoch_curr_time1 = datetime.datetime.now()
        # initial state
        state_init_arr = env.state_array(env.state_init)
        state = (env.state_init).copy()
        costs = env.cost_init
        sum = 0
        reward_list = [0]
        state_arr_for_one = state_init_arr
        reward = init_reward
        ep_r = 0
        while True:
            state_list = list(chain.from_iterable(np.array(state)))
            action, is_random = dqn.choose_action(state_list)
#             # take action
            state_, costs_, reward_, cost_all = env.step(action, state, costs)
            state__arr = env.state_array(state_)
            different = [y for y in (state_arr_for_one + state__arr) if y not in state_arr_for_one]
            # print("different:", different)
            if ((reward_ < init_reward and reward_ < min(reward_list)) or
                    (not is_random and len(different) == 0 and reward_ >= reward and reward_ > (init_reward))):
                done = True
            else:
                done = False

            if reward_ < init_reward and reward_ < min(reward_list):
                print("负结束")

            if not is_random and len(different) == 0 and reward_ >= reward and reward_ > (init_reward):
                print("正结束")

            reward = reward_
            reward_list.append(reward)
            dqn.store_transition(state_list, action, reward, list(chain.from_iterable(np.array(state_))))
            ep_r += reward
            if dqn.memory_counter > MEMORY_CAPACITY:
                # print("learn")
                dqn.learn()
            sum += 1

            if done:
                break

            state_arr_for_one = state__arr
            costs = costs_
            state = state_
        reward_all_list.append(reward)
        epoch_curr_time2 = datetime.datetime.now()
        epoch_time = epoch_curr_time2 - epoch_curr_time1
        cost_all_list.append(cost_all)
        print("epoch:", i_episode+1)
        print("The number of cycles in this epoch：", sum)
        # print("The best reward in this epoch：", max(reward_list))
        print("The final reward in this epoch:", reward)

    print("训练完成数组：" + str(state_arr_for_one))
    list_query = []
    list_all_servers = [[]for i in range(8)]
    list_all_del = []

    # Get the query corresponding to the server
    for item in state_arr_for_one:
        list_query.append(item[0])
        list_all_servers[item[1]].append(item[0])
    all_query_file_path = "indexFile/all_query_file_train.txt" #Total nodes
    f1 = open(all_query_file_path, "w")
    f1.write(str(list_query))
    f1.close()

    # Get the deleted nodes
    server_query_file_path = "indexFile/server_query_file_train.txt"
    f = open(server_query_file_path,"w")
    for list_server in list_all_servers:
        list_all_servers_del = [item1 for item1 in list_query if not item1 in list_server]
        f.write(str(list_all_servers.index(list_server)))
        f.write(",")
        f.write(str(list_all_servers_del))
        f.write("\n")
        list_all_del.append(list_all_servers_del)
    f.close()


    print("------------------------")
    improve = ((reward_all_list[-1] - init_reward)/init_reward)*100
    print("The improve percent:", improve, "%")

    reward_all_list_np = np.array(reward_all_list)
    y_all_list = np.mean(reward_all_list_np.reshape(-1, 100), axis=1)
    print("y_all_list:", len(y_all_list))
    x = (np.arange(len(y_all_list)))
    y = y_all_list
    y1 = [init_reward]*len(x)
    fig = plt.Figure(figsize=(24, 20))
    pl.plot(x, y, label=u'RL')
    pl.legend()
    pl.plot(x, y1, label=u'Init')
    pl.legend()
    pl.xlabel(u"epoch", size=14)
    pl.ylabel(u"reward", size=14)
    # fig_name = "/Users/fengxiaoyue/Desktop/trainingResult.png"
    fig_name = "trainingResult/test-file.png"
    plt.savefig(fig_name,dpi = 400)
    plt.show()
    time.sleep(10)
    curr_time2 = datetime.datetime.now()
    train_time = curr_time2-curr_time1
    print("The training time：", train_time)
    print("reward: ", reward)
    finished = "Train finished"
    while True:
        pic_if = os.path.exists(fig_name)
        if pic_if:
            img_stream = return_img_stream(fig_name)#图片流
            break
        else:
            time.sleep(10)
    return img_stream

@app.route("/get_comparison/", methods=['GET', "POST"])
def get_comparison():
    while True:
        pic_if = os.path.exists('comparison/figure_estimated_1.jpg')
        if pic_if:
            img_stream = return_img_stream('comparison/figure_estimated_1.jpg')#图片流
            break
        else:
            time.sleep(10)

    return img_stream


@app.route("/get_indexFile/", methods=['GET', "POST"])
def get_indexFile():
    all_query_file_path = "indexFile/all_query_file_train.txt"
    server_query_file_path = "indexFile/server_query_file_train.txt"
    list = []
    list1 = []
    list2 = []
    while True:
        file_if_1 = os.path.exists(all_query_file_path)
        file_if_2 = os.path.exists(server_query_file_path)
        if (file_if_1 and file_if_2):
            file1 = all_query_file_path
            file2 = server_query_file_path
            with open(file1) as files:
                all_query_file_text = files.readlines()
                # list.append(all_query_file_text)
                # print(type(all_query_file_text))
            with open(file2) as files:
                server_query_file_text = []
                while True:
                    server_query_file_text_line_1 = files.readline()
                    if server_query_file_text_line_1:
                        server_query_file_text_line = server_query_file_text_line_1.strip('\n')
                        print(server_query_file_text_line)
                        server_query_file_text.append(server_query_file_text_line)
                    else:
                        break
                print(type(server_query_file_text))
            break
        else:
            time.sleep(10)
    list.append(all_query_file_text)
    list.append(server_query_file_text)
    print(all_query_file_text)
    print(server_query_file_text)
    return list

if __name__ == '__main__':
              
    init_state = state_init(DataSet_filePath)
    QSs = read_file(DataSet_filePath)
    env = Cluster(init_state, server_attribute, QSs, server_number)
    N_ACTIONS = len(env.action_space)
    N_STATES = len(env.state_init)*len(env.state_init.columns)
    # ENV_A_SHAPE = 0 if isinstance(env.action_space.sample(), int) else env.action_space.sample().shape
    device = torch.device('cuda:0'if torch.cuda.is_available() else "cpu")
    curr_time1 = datetime.datetime.now()
    dqn = DQN(N_STATES, N_ACTIONS, device)
    print('\nCollecting experience...')
    cost_all_list = []
    reward_all_list = []
    init_reward = env.reward(env.cost_all(env.cost_init), env.state_init)
    for i_episode in range(80000):
        print("数据集是泊松分布,reward是两个tanh")
        epoch_curr_time1 = datetime.datetime.now()
        # initial state
        state_init_arr = env.state_array(env.state_init)
        state = (env.state_init).copy()
        costs = env.cost_init
        sum = 0
        reward_list = [0]
        state_arr_for_one = state_init_arr
        reward = init_reward
        ep_r = 0

        while True:
            state_list = list(chain.from_iterable(np.array(state)))
            action, is_random = dqn.choose_action(state_list)
            # print(action)
            # take action
            state_, costs_, reward_, cost_all, action = env.step(action, state, costs)
            state__arr = env.state_array(state_)
            different = [y for y in (state_arr_for_one + state__arr) if y not in state_arr_for_one]
            # print("different:", different)
            if ((reward_ < init_reward and reward_ < min(reward_list)) or
                    (not is_random and len(different) == 0 and reward_ >= reward and reward_ > (init_reward))):
                done = True
            else:
                done = False
            
            if reward_ < init_reward and reward_ < min(reward_list):
                print("负结束")
            
            if not is_random and len(different) == 0 and reward_ >= reward and reward_ > (init_reward):
                print("正结束")
            
            reward = reward_
            reward_list.append(reward)
            dqn.store_transition(state_list, action, reward, list(chain.from_iterable(np.array(state_))))
            ep_r += reward
            if dqn.memory_counter > MEMORY_CAPACITY:
                # print("learn")
                dqn.learn()
            sum += 1
        
            if done:
                break
        
            state_arr_for_one = state__arr
            costs = costs_
            state = state_
        reward_all_list.append(reward)
        epoch_curr_time2 = datetime.datetime.now()
        epoch_time = epoch_curr_time2 - epoch_curr_time1
        cost_all_list.append(cost_all)
        print("epoch:", i_episode+1)
        print("The number of cycles in this epoch：", sum)
        # print("The best reward in this epoch：", max(reward_list))
        print("The final reward in this epoch:", reward)


    print("------------------------")
    improve = ((reward_all_list[-1] - init_reward)/init_reward)*100
    print("The improve percent:", improve, "%")
    
    reward_all_list_np = np.array(reward_all_list)
    y_all_list = np.mean(reward_all_list_np.reshape(-1, 100), axis=1)
    print("y_all_list:", len(y_all_list))
    x = (np.arange(len(y_all_list)))
    y = y_all_list
    y1 = [init_reward]*len(x)
    fig = plt.Figure(figsize=(24, 20))
    pl.plot(x, y, label=u'RL')
    pl.legend()
    pl.plot(x, y1, label=u'Init')
    pl.legend()
    pl.xlabel(u"epoch", size=14)
    pl.ylabel(u"reward", size=14)
    plt.savefig("longtail-600-0.0005-2.png", dpi=500)
    plt.show()
    curr_time2 = datetime.datetime.now()
    train_time = curr_time2-curr_time1
    print("The training time：", train_time)
    print("reward: ", reward)



def generate_server(list_var_small_all, probabilities_list):
    if not (0.85 < sum(probabilities_list) < 1.15):
        raise ValueError("The probabilities are not normalized!")
    if len(list_var_small_all) != len(probabilities_list):
        raise ValueError("The length of two input lists are not match!")

    random_normalized_num = random.random()  # random() -> x in the interval [0, 1).
    accumulated_probability = 0.0
    for item in zip(list_var_small_all, probabilities_list):
        accumulated_probability += item[1]
        if random_normalized_num < accumulated_probability:
            return item[0]

def local_cost():
    episode = 5000
    QSs = read_file(DataSet_filePath)
    for i in range(episode):
        init_state_local = state_init(DataSet_filePath)
        env_local = Cluster(init_state_local, server_attribute, QSs, server_number)
        # print("cost_all", env.cost_all((env.cost_init)))
        N_ACTIONS = len(env_local.action_space)
        N_STATES = len(env_local.state_init)*len(env_local.state_init.columns)
        curr_time1 = datetime.datetime.now()
        init_reward_list = []
        init_reward, init_list_var, load_weight_var = env_local.reward_local(env_local.cost_all(env_local.cost_init), env_local.state_init)
        init_reward_list.append(init_reward)
        print("init_reward:", init_reward)
        print("init_list_var", init_list_var)
        print("初始方差", load_weight_var)

    costs = env_local.cost_init
    state = (env_local.state_init).copy()

    # 对QSs中的数据进行遍历，按照本地化的原则在属性所在的服务器中选择一个reward最大的服务器
    for item in QSs:
        action = []
        action.append(item[0])
        reward_list = []
        for i in item[1]:
            action.append(i)
            state_, costs_, reward_, cost_all, list_var, load_weight_var = env.step(action, state, costs)
            reward_list.append(reward_)
        max_index = reward_list.index(max(reward_list))
        action.append(item[1][max_index])
        state_, costs_, reward_, cost_all, list_var, load_weight_var = env.step(action, state, costs)
        state = state_
        costs = costs_
    print("未考虑负载均衡时的方差", load_weight_var)
    print("未考虑负载均衡时用于求方差的数组", list_var)
    print("reward_:", reward_)

    for n in range(5):
        # 将数据集中的众数（也就是出现次数最多的元素）去除，以免影响方差
        attri = []
        for item in QSs:
            attri.append(item[1])
        x = dict((tuple(a), attri.count(a)) for a in attri)
        y = [k for k, v in x.items() if max(x.values()) == v]
        y = [list(item) for item in y]
        index = []
        for j in y:
            for i, item in enumerate(attri):
                if item == j:
                    index.append(i)
        # print("index的数量", len(index))

        QSs_mode = [QSs[i] for i in range(len(QSs)) if i in index]
        QSs = [QSs[i] for i in range(len(QSs)) if i not in index]
        # 把出现数量多的属性组合对应的查询分配给目前数量少的服务器
        list_var_mean = sum(list_var)/len(list_var)
        for item in QSs_mode:
            list_var_small_all = []
            list_var_small_value = []
            for server, value in enumerate(list_var):
                list_var_small = []
                if value < list_var_mean:
                    list_var_small.append(server)
                    list_var_small.append(value)
                    list_var_small_value.append(1/value)
                    list_var_small_all.append(list_var_small)
            print("list_var_small_all", list_var_small_all)
            action = []
            action.append(item[0])
            probabilities_list = []
            for i in list_var_small_value:
                probabilities = i/sum(list_var_small_value)
                probabilities_list.append(probabilities)
            print(probabilities_list)
            list_var_small_generate = generate_server(list_var_small_all, probabilities_list)
            server = int(list_var_small_generate[0])
            action.append(server)
            print("action", action)
            state_, costs_, reward_, cost_all, list_var, load_weight_var = env.step(action, state, costs)
            state = state_
            costs = costs_
        print("方差_%d: %f" % (n, load_weight_var))
        print("用于求方差的数组_%d: %s" % (n, str(list_var)))
        print("reward_:", reward_)


    improve = ((reward_ - (sum(init_reward_list)/len(init_reward_list)))/(sum(init_reward_list)/len(init_reward_list)))*100
    print("init_reward_average:", sum(init_reward_list)/len(init_reward_list))
    print("The improve percent:", improve, "%")
    f = open("D:\\SynologyDrive\\Paper\\BIP\\Experiment\\实验结果_查询数100\\init_reward_longtail.txt", "a")
    f.write("reward:"+str(reward_)+"\n" +
            "init_reward_average:"+str(sum(init_reward_list)/len(init_reward_list))+"\n" +
            "improve:"+str(improve)+"%")
    f.close()

    curr_time2 = datetime.datetime.now()
    calculate_time = curr_time2-curr_time1
    print("The calculate time：", calculate_time)
    return reward_
#
# if __name__ == '__main__':
#     # get_indexFile()
#     app.run(host='0.0.0.0', port=50008)