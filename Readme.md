# Chisel Template

[![Scala CI](https://github.com/carlosedp/chisel-template/actions/workflows/scala.yml/badge.svg)](https://github.com/carlosedp/chisel-template/actions/workflows/scala.yml)

This is template project to demonstrate [Chisel](https://www.chisel-lang.org/) functionality with build scripts and tooling. The build is handled by Scala [mill](https://com-lihaoyi.github.io/mill/mill/Intro_to_Mill.html) with some make targets for ease-of-use.

The project includes a simple module (to be replaced with your design) and also have a test spec written with the [scalatest](https://www.scalatest.org/) and [chiseltest](https://github.com/ucb-bar/chiseltest) frameworks. The repository also have a GitHub Action to run automated tests on main branch and PRs.

Chisel Learning Resources:

- [Chisel Book](https://github.com/schoeberl/chisel-book)
- [Chisel Documentation](https://www.chisel-lang.org/chisel3/)
- [Chisel API](https://www.chisel-lang.org/api/latest/chisel3/index.html)

Software requirements:

- Java JDK
- Verilator (as an option for simulation)
- GTKWave (to visualize VCD files)

## Generating Verilog

Verilog code can be generated from Chisel by using the `chisel` Makefile target.

```sh
make chisel
```

The output verilog files are generated in the `./generated` directory.

Running tests can be done with:

```sh
make test
```

More targets can be listed by running `make`.
