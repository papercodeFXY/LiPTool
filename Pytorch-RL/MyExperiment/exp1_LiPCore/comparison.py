import numpy as np
import matplotlib.pyplot as plt
import pylab as pl
from mpl_toolkits.axes_grid1.inset_locator import inset_axes

epoch=[50,100,200,300,400,500]
fig1, (ax1,ax2) = plt.subplots(nrows=1, ncols=2,figsize = (9,4.8))
# fig1, (ax1) = plt.subplots(nrows=1, ncols=1,figsize = (4.5,4))
# fig1, (ax2) = plt.subplots(nrows=1, ncols=1,figsize = (4.5,4))

#Longtail
# acc=[55,50,47,45,43]
acc=[54,50,47,45,43,42.5] #LiPCore-lr0.0005
x=epoch
y=acc

# acc=[55,50,47,45,43]
acc=[53.7,48,45,43,41,40] #LiPCore-lr0.001
x_1=epoch
y_1=acc

# acc=[55,50,47,45,43]
acc=[52,47,43,40,38,37] #LiPCore-lr0.0001
x_2=epoch
y_2=acc

# acc=[50,47.7,39,27.8,12.6]
acc=[50,45.7,34.2,22.4,13.6,12] #CG-index
x1=epoch
y1=acc

# acc=[34,27,21,14.8,6.7]
acc=[34,29,21,14.8,8.7,7.6] #baseline
x2=epoch
y2=acc

font1 = {
    'family':'Times New Roman',
    'weight':'normal',
    'size':'14'
}

font2 = {
    'family':'Times New Roman',
    'weight':'normal',
    'size':'16'
}

plt.sca(ax1)
plt.ylim(0,70)
p1 = plt.plot(x,y,marker='*',color='#2E8B57',label='LiPCore-0.0005')
plt.legend(
    #     handles = p1,prop = font1
)
p_1 = plt.plot(x_1,y_1,marker='*',color='#483D8B', linestyle='dashed', label='LiPCore-0.001')
plt.legend(
    #     handles = p1,prop = font1
)
p_2 = plt.plot(x_2,y_2,marker='*',color='#0000FF', linestyle='dashed', label='LiPCore-0.0001')
plt.legend(
    #     handles = p1,prop = font1
)

p2 = plt.plot(x1,y1,marker='o',color='#FF0000',label='CG-index')
plt.legend(
    #     handles = p2,prop = font1
)
p3 = plt.plot(x2,y2,marker='^',color='#FFA500',label='Baseline')
plt.legend(
    #     handles = p3,prop = font1
)
plt.xlabel(u'The query scale',font1)
plt.ylabel(u'Average Query Performance',font1)
ax1.set_title("(a)Longtail",font2,y=-0.25)
plt.tight_layout()
# plt.savefig('figure_estimated_longtail.jpg',dpi=500,bbox_inches = 'tight')
# plt.show()

#Poisson
# acc=[56,50,46,44.5,42, 41.5]
acc=[56,50,46,44.5,42.5, 41.5] #LiPCore-0.0005
x=epoch
y=acc

# acc=[56,50,46,44.5,42, 41.5]
acc=[54,48.3,44.4,42,39.5,38.6] #LiPCore-lr0.001
x_1=epoch
y_1=acc

# acc=[56,50,46,44.5,42, 41.5]
acc=[52,46.7,42.3,39.3,37,36] #LiPCore-lr0.0001
x_2=epoch
y_2=acc

# acc=[50,46,40,30,22]
acc=[50,46,39,30,22,19] #CG-index
x1=epoch
y1=acc

# acc=[31,29.4,27,14.3,7.5]
acc=[31,28.5,22,14.3,7.5,6.9] #baseline
x2=epoch
y2=acc

plt.sca(ax2)
plt.ylim(0,70)
pl1 = plt.plot(x,y,marker='*',color='#2E8B57',label='LiPCore-0.0005')
plt.legend(
    #     handles = pl1,prop = font1
)
pl_1 = plt.plot(x_1,y_1,marker='*',color='#483D8B', linestyle='dashed', label='LiPCore-0.001')
plt.legend(
    #     handles = pl1,prop = font1
)
pl_2 = plt.plot(x_2,y_2,marker='*',color='#0000FF', linestyle='dashed', label='LiPCore-0.0001')
plt.legend(
    #     handles = pl1,prop = font1
)
pl2 = plt.plot(x1,y1,marker='o',color='#FF0000',label='CG-index')
plt.legend(
    #     handles = pl2,prop = font1
)
pl3 = plt.plot(x2,y2,marker='^',color='#FFA500',label='Baseline')
plt.legend(
    #     handles = pl3,prop = font1
)
plt.xlabel(u'The query scale',font1)
# plt.ylabel(u'Total Estimated Cost',font1)
ax2.set_title("(b)Poisson",font2,y=-0.25)
plt.tight_layout()
# plt.savefig('figure_estimated_poisson.jpg',dpi=500,bbox_inches = 'tight')
plt.savefig('comparison/figure_estimated_1.jpg',dpi=500,bbox_inches = 'tight')
plt.show()
# plt.title('Compare accuracy for different models in training')