# Chisel Template

[![Scala CI](https://github.com/carlosedp/chisel-template/actions/workflows/scala.yml/badge.svg)](https://github.com/carlosedp/chisel-template/actions/workflows/scala.yml)

This is a simple template project to demonstrate Chisel functionality with build scripts
and tooling.

The project includes a simple module (to be replaced with your design) and also have a test spec. The repository also add a GitHub Actions CI to run automated tests on main branch and PRs.

Software requirements:

- Java JDK
- Verilator (as an option for simulation)
- GTKWave (to visualize VCD files)

## Generating Verilog

Verilog code can be generated from Chisel by using the `chisel` Makefile target.

```sh
make chisel
```

Running tests can be done with:

```sh
make test
```

More targets can be listed by running `make`.
