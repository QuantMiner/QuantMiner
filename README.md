QuantMiner
==========

QuantMiner is a Data Mining tool for mining Quantitative Association Rules that is taking into consideration numerical attributes in the mining process without a binning/discretization a priori of the data. It exploits a recent and innovative research in using genetic algorithms for mining quantitative rules published in IJCAI 2007. We hope you will find it useful and welcome your feedback.

If you publish material based on QuantMiner, then, please note the use of QuantMiner and its url. This will help others to obtain the system and replicate your experiments. We suggest the following reference format for referring to this software:

Salleb-Aouissi, A. Vrain, C. Nortet, C. (2007). QuantMiner: A Genetic Algorithm for Mining Quantitative Association Rules. In the Proceedings of the 20th International Conference on Artificial Intelligence IJCAI 2007, pp. 1035-1040. Hyberadad, India. 

Here are the BiBTeX citations:

```
@inproceedings{SallebVrainNortet2007,
  author = {Ansaf Salleb-Aouissi and
  Christel Vrain and Cyril Nortet},
  title = {QuantMiner: A Genetic Algorithm for Mining Quantitative Association Rules}, booktitle = {IJCAI},
  year = {2007},
  pages = {1035-1040},
  ee = {http://dli.iiit.ac.in/ijcai/IJCAI-2007/PDF/IJCAI07-167.pdf}
}
```
```
@article{JMLR:v14:salleb-aouissi13a,
  author  = {Ansaf Salleb-Aouissi and Christel Vrain and Cyril Nortet and Xiangrong Kong and Vivek Rathod and Daniel Cassard},
  title   = {QuantMiner for Mining Quantitative Association Rules},
  journal = {Journal of Machine Learning Research},
  year    = {2013},
  volume  = {14},
  pages   = {3153-3157},
  url     = {http://jmlr.org/papers/v14/salleb-aouissi13a.html}
}
```

Contribution
====
- To file bugs found in the software create a github issue.
- To contribute code for fixing filed issues create a pull request with the issue id.
- To propose a new feature open a discussion on the mailing list.

Build
====
The project depends on sun JRE 6. To build use the ant build file with "dist" as target.
To build javadocs for the project use the ant build file with "doc" as target

Copyright
====
Copyright 2007 CCLS Columbia University (USA), LIFO University of Orleans (France), BRGM (France). Authors: Cyril Nortet, Xiangrong Kong, Ansaf Salleb-Aouissi, Christel Vrain, Daniel Cassard
QuantMiner is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
QuantMiner is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

