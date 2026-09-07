def AND(x1, x2):
    w1, w2, theta = 0.5, 0.5, 0.7
    tmp = x1*w1 + x2*w2
    if tmp <= theta:
        return 0
    else:
        return 1

print(f"x1=0, x2=0, y={AND(0,0)}")
print(f"x1=1, x2=0, y={AND(1,0)}")
print(f"x1=0, x2=1, y={AND(0,1)}")
print(f"x1=1, x2=1, y={AND(1,1)}")
