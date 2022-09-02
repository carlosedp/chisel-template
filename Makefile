# Source and target files/directories
project = $(shell grep object build.sc |tail -1 |cut -d" " -f2 |xargs)
scala_files = $(wildcard src/main/scala/*.scala)
generated_files = generated					# Destination directory for generated files
BUILDTOOL ?= mill 							# Can also be mill

# Toolchains
DOCKERARGS  = run --rm -v $(PWD):/src -w /src
SBTIMAGE   = docker $(DOCKERARGS) adoptopenjdk:8u282-b08-jre-hotspot
SBTCMD   = $(SBTIMAGE) curl -Ls https://git.io/sbt > /tmp/sbt && chmod 0775 /tmp/sbt && /tmp/sbt
SBTLOCAL := $(shell command -v sbt 2> /dev/null)
ifndef SBTLOCAL
    SBT=${SBTCMD}
else
	SBT=sbt
endif

# Define utility applications
# VERILATOR= docker $(DOCKERARGS) hdlc/verilator verilator	# Docker Verilator
VERILATOR=verilator  # Local Verilator
YOSYS = docker $(DOCKERARGS) hdlc/yosys yosys

# Default board PLL and parameters to be passed to Chisel (require parsing at Toplevel)
BOARD := bypass
#	BOARDPARAMS=-board ${BOARD} -cpufreq 25000000 -invreset false

# Targets
chisel: $(generated_files) ## Generates Verilog code from Chisel sources (output to ./generated)

$(generated_files): $(scala_files) build.sc build.sbt
	@rm -rf $(generated_files)
	@test "$(BOARD)" != "bypass" || (echo "Set BOARD variable to one of the supported boards: " ; test -f file.core && cat file.core|grep "\-board" |cut -d '-' -f 2|sed s/\"//g | sed s/board\ //g |tr -s '\n' ','| sed 's/,$$/\n/'; echo "Eg. make chisel BOARD=ulx3s"; echo; echo "Generating design with bypass PLL..."; echo)
	@if [ $(BUILDTOOL) = "sbt" ]; then \
		${SBT} "run --target:fpga -td $(generated_files) $(BOARDPARAMS)"; \
    elif [ $(BUILDTOOL) = "mill" ]; then \
		scripts/mill $(project).run --target:fpga -td $(generated_files) $(BOARDPARAMS); \
	fi

chisel_tests:
	@if [ $(BUILDTOOL) = "sbt" ]; then \
		${SBT} "test"; \
    elif [ $(BUILDTOOL) = "mill" ]; then \
		scripts/mill $(project).test; \
	fi
	@echo "If using WriteVcdAnnotation in your tests, the VCD files are generated in ./test_run_dir/testname directories."

test: chisel_tests ## Run Chisel tests
check: chisel_tests

fmt: ## Formats code using scalafmt and scalafix
	${SBT} lint

MODULE ?= Toplevel
dot: $(generated_files) ## Generate dot files for Core
	@echo "Generating graphviz dot file for module \"$(MODULE)\". For a different module, pass the argument as \"make dot MODULE=mymod\"."
	@$(YOSYS) -p "read_verilog ./generated/*.v; proc; opt; show -colors 2 -width -format dot -prefix $(MODULE) -signed $(MODULE)"

clean:   ## Clean all generated files
	@if [ $(BUILDTOOL) = "sbt" ]; then \
		${SBT} "clean"; \
    elif [ $(BUILDTOOL) = "mill" ]; then \
		scripts/mill clean; \
	fi
	@rm -rf obj_dir test_run_dir target
	@rm -rf $(generated_files)
	@rm -rf out
	@rm -f *.mem

cleancache: clean  ## Clean all downloaded dependencies and cache
	@rm -rf project/.bloop
	@rm -rf project/project
	@rm -rf project/target
	@rm -rf .bloop .bsp .metals .vscode

help:
	@echo "Makefile targets:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = "[:##]"}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$4}'
	@echo ""

.PHONY: chisel_tests clean help
.DEFAULT_GOAL := help
