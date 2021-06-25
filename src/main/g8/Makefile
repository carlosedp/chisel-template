SHELL = bash
# Project name and toplevel
project = toplevel
toplevel = Toplevel

scala_files = \$(wildcard src/main/scala/*scala)
generated_files = generated
verilog_files = \$(generated_files)/*.v

# Targets
chisel: \$(verilog_files)   ## Generates Verilog code from Chisel sources using Mill
\$(verilog_files): \$(scala_files) check-board-vars clean
	scripts/mill \$(project).run -board \${BOARD} -td \$(generated_files)

chisel-sbt:  ## Generates Verilog code from Chisel sources using SBT
	sbt "run -board \${BOARD} -td \$(generated_files)"

chisel_tests:
	scripts/mill \$(project).test

chisel_tests_sbt:
	sbt "test"

check: chisel_tests ## Run Chisel tests using Mill
check-sbt: chisel_tests_sbt ## Run Chisel tests using SBT

fmt:
	scripts/mill all \$(project).{reformat,fix}

check-board-vars:
	@test -n "\$(BOARD)" || (echo "Set BOARD variable to one of the supported boards: " ; cat chiselblinky.core|grep "\-board" |cut -d '-' -f 2|sed s/\"//g | sed s/board\ //g |tr -s '\n' ','| sed 's/,\$\$/\n/'; echo "Eg. make chisel BOARD=ulx3s"; echo; exit 1)

clean:   ## Clean all generated files
	@./scripts/mill clean
	@rm -rf obj_dir test_run_dir target
	@rm -rf \$(generated_files)
	@rm -rf out
	@rm -f \$(project)

help:
	@echo "Makefile targets:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*\$\$' \$(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = "[:##]"}; {printf "\033[36m%-20s\033[0m %s\n", \$\$1, \$\$4}'
	@echo ""

.PHONY: chisel clean prog help
.DEFAULT_GOAL := help
