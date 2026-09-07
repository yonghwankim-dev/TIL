import numpy as np
import matplotlib.pylab as plt

def numerical_diff(f, x):
    h = 1e-4 # 0.0001
    return (f(x+h) - f(x-h)) / (2*h)

def function_1(x):
    return 0.01*x**2 + 0.1*x

def function_2(x):
    return x[0]**2 + x[1]**2 # np.sum(x**2)

def function_tmp1(x0):
    return x0*x0 + 4.0**2.0

def function_tmp2(x1):
    return 3.0**2.0 + x1*x1

def numerical_gradient(f, x):
    h = 1e-4 # 0.0001
    grad = np.zeros_like(x) # x와 형상이 같은 배열을 생성

    for idx in range(x.size):
        tmp_val = x[idx]
        # f(x+h) 계산
        x[idx] = tmp_val + h
        fxh1 = f(x)

        # f(x-h) 계산
        x[idx] = tmp_val - h
        fxh2 = f(x)

        grad[idx] = (fxh1 - fxh2) / (2*h)
        x[idx] = tmp_val # 값 복원
    return grad

x = np.arange(0.0, 20.0, 0.1)
y = function_1(x)
plt.xlabel("x")
plt.ylabel("f(x)")
plt.plot(x, y)
plt.show()

print(f"x=5, df(x)/f(x)={numerical_diff(function_1,5)}")
print(f"x=10, df(x)/f(x)={numerical_diff(function_1,10)}")

print(f"x0=3, x1=4, x0 편미분={numerical_diff(function_tmp1, 3.0)}")
print(f"x0=3, x1=4, x1 편미분={numerical_diff(function_tmp2, 4.0)}")

print(f"(3,4) 위치에 대한 편미분 기울기={numerical_gradient(function_2, np.array([3.0, 4.0]))}")
print(f"(0,2) 위치에 대한 편미분 기울기={numerical_gradient(function_2, np.array([0.0, 2.0]))})")
print(f"(3,0) 위치에 대한 편미분 기울기={numerical_gradient(function_2, np.array([3.0, 0.0]))})")
