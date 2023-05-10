# Ubiquitous Neural Networks

Ubiquitous Neural Networks is a framework and collection of neural network models that stemmed from my
[PhD Thesis](http://hdl.handle.net/10362/19974). The original code was completely refactored and is
now made available through this project. 

It allows you to easily build and train neural networks in Java. 
It includes a variety of features and tools to help you create powerful and accurate models for your data.

![](assets/logo.svg)

## Features

### Models

This project implements three types of **neural network models and variants**:

- **`SelfOrganizingMap`** (SOM): a type of neural network that projects high-dimensional data onto a 2d map, maintaining input-space topological relationships. Useful for exploratory cluster analysis and, possibly, classification.
  - `BasicSOM` - *Classical* and *Batch* algorithms are available;
  - For *streaming data*, i.e., variants that can learn incrementally over time, estimating its learning parameters on-the-fly:
    - `UbiSOM` - The Ubiquitous Self-Organizing Map;
    - `PLSOM` - The Parameterless Self-Organizing Map;
    - `DSOM` - The Dynamic Self-Organizing Map.

  > All SOM models allow setting the lattice type (hexagonal or rectangular) and the metric distance to use (euclidean or manhattan); but you
  > can easily create your own lattices and metric distances. 

- **`StreamART2A`**: a neural network architecture, based on Adaptive Resonance Theory, that uses a process of competitive learning and resonance to summarize input patterns in streaming data.

- **`MLPNetwork` (Multilayer Perceptron)**: a feedforward neural network architecture consisting of multiple layers of nodes that can approximate any function with enough hidden units. Useful for classification and regression.

  > You can create deep learning models and comes with several activation functions, e.g., linear, ReLU, sigmoid and tanh.

### Data and preprocessing

- Data is imported through the `Dataset` class. Data set files must have a *yaml* header describing it - see examples in the `datasets` folder;
- There are two implementations of `DatasetNormalization`, namely `MinMaxNormalization` and `MeanNormalization`. You can add others;
- The class `DatasetTrainSplit`, as the name suggests, allows you to easily split your data for training and testing;
- The library comes with an implementation of `PCA` data projection.

### Visualizations

Contains different types of visualizations for all available models and a simple plotting class.

#### Self-Organizing Maps

![](assets/som-visualizations-example.png)

#### StreamART2A

![](assets/streamart2a-visualizations-example.png)

#### MLP Networks

![](assets/mlp-visualizations-example.png)

## Documentation

### Installation

:construction: Library to be made available through Maven Central.

### Usage

:construction: Until the *Wiki* pages are created with examples, you can check some examples in the `com.brunomnsilva.ubiquitousneuralnets.examples` source folder.



### API Reference

:construction: API reference to be made available through <https://javadoc.io/>, after Maven Central artifact.

## Examples

You can check the `examples` source folder and the Wiki.

## Contributing

You can clone the repository and submit a pull request. Pull requests should adhere to the existing naming and *Javadoc* conventions.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details. 
All derivative work should include this license.

## Authors

Original author: **Bruno Silva** - [(GitHub page)](https://github.com/brunomnsilva) | [(Personal page)](https://www.brunomnsilva.com/)

### Contributors

(no others, at the moment)

---

I hope you find Ubiquitous Neural Networks useful and look forward to seeing the projects you create with it!