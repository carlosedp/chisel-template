# Source and target files/directories
project = $(shell grep object build.sc |tail -1 |cut -d" " -f2 |xargs)
scala_files = $(wildcard $(project)/*/*.scala)
generated_files = generated					# Destination directory for generated files

# Toolchains and tools
MILL = ./mill
DOCKERARGS  = run --rm -v $(PWD):/src -w /src
# Define utility applications
# VERILATOR= docker $(DOCKERARGS) hdlc/verilator verilator	# Docker Verilator
VERILATOR=verilator  # Local Verilator
YOSYS = docker $(DOCKERARGS) hdlc/yosys yosys

# Default board PLL and parameters to be passed to Chisel (require parsing at Toplevel)
BOARD := bypass
#	BOARDPARAMS=-board ${BOARD} -cpufreq 25000000 -invreset false

# Targets
chisel: $(generated_files) ## Generates Verilog code from Chisel sources (output to ./generated)

$(generated_files): $(scala_files) build.sc #checkboard
	@rm -rf $(generated_files)
	$(MILL) $(project).run --target:fpga -td $(generated_files) $(BOARDPARAMS)

checkboard:
	@test "$(BOARD)" != "bypass" || (echo "Set BOARD variable to one of the supported boards: " ; test -f file.core && cat file.core|grep "\-board" |cut -d '-' -f 2|sed s/\"//g | sed s/board\ //g |tr -s '\n' ','| sed 's/,$$/\n/'; echo "Eg. make chisel BOARD=ulx3s"; echo; echo "Generating design with bypass PLL..."; echo)

check: test
test: ## Run Chisel tests
	$(MILL) $(project).test
	@echo "If using WriteVcdAnnotation in your tests, the VCD files are generated in ./test_run_dir/testname directories."


lint: ## Formats code using scalafmt and scalafix
	$(MILL) lint

deps: ## Check for library version updates
	$(MILL) deps

MODULE ?= Toplevel
dot: $(generated_files) ## Generate dot files for Core
	@echo "Generating graphviz dot file for module \"$(MODULE)\". For a different module, pass the argument as \"make dot MODULE=mymod\"."
	@$(YOSYS) -p "read_verilog ./generated/*.v; proc; opt; show -colors 2 -width -format dot -prefix $(MODULE) -signed $(MODULE)"

clean:   ## Clean all generated files
	$(MILL) clean
	@rm -rf obj_dir test_run_dir target
	@rm -rf $(generated_files)
	@rm -rf out
	@rm -f *.mem

cleanall: clean  ## Clean all downloaded dependencies, cache and generated files
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
