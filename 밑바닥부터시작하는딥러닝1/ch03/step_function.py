import numpy as np
import matplotlib.pylab as plt

def step_function(x):
    return np.array(x > 0, dtype=int)

print(f"x=-1, h(x)={step_function(-1)}")
print(f"x=0, h(x)={step_function(0)}")
print(f"x=1, h(x)={step_function(1)}")
print(f"x=[-1.0, 1.0, 2.0], h(x)={step_function(np.array([-1.0, 1.0, 2.0]))}")

x = np.arange(-5.0, 5.0, 0.1)
y = step_function(x)
plt.plot(x, y)
plt.ylim(-0.1, 1.1) # y축 범위 지정
plt.show()
