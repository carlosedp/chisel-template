# Chisel Template

This is a simple template project to demonstrate Chisel functionality with build scripts
and tooling.

The project includes a simple module (to be replaced with your design) and also have a test spec.

## Generating Verilog

Verilog code can be generated from Chisel by using the `chisel` Makefile target.

```sh
make chisel
```

Running tests can be done with:

```sh
make test
```

The project supports building with SBT (Scala Build Tool) by default but also Mill if passing the parameter to make like `make chisel BUILDTOOL=mill` or `make test BUILDTOOL=mill`.
