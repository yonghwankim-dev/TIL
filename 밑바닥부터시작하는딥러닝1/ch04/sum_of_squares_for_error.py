import numpy as np

def sum_squares_error(y, t):
    return 0.5 * np.sum((y-t)**2)

# 정답은 '2'
t = [0, 0, 1, 0, 0, 0, 0, 0, 0, 0]

# 예1: '2'일 확률이 가장 높다고 추정함(0.6)
y = [0.1, 0.05, 0.6, 0.0, 0.05, 0.1, 0.0, 0.1, 0.0, 0.0]
print(f"sum_squares_error(np.array(y), np.array(t))={sum_squares_error(np.array(y), np.array(t))}")

# 예2: '7'일 확률이 가장 높다고 추정함(0.6)
y = [0.1, 0.05, 0.1, 0.0, 0.05, 0.1, 0.0, 0.6, 0.0, 0.0]
print(f"sum_squares_error(np.array(y), np.array(t))={sum_squares_error(np.array(y), np.array(t))}")
