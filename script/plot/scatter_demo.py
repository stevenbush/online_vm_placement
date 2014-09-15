"""
Simple demo of a scatter plot.
"""
import numpy as np
import matplotlib.pyplot as plt


x = np.random.randn(1000)
y = np.random.randn(1000)

plt.scatter(x,y)
plt.plot([0,0.25,0.5], [0,0.5,1])
plt.plot([0,0.5,1], [0,0.25,0.5])
plt.xlim(0, 1)
plt.ylim(0, 1)
plt.savefig("plot.pdf", dpi=150)
plt.show()
