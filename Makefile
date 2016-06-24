# Copyright (C) 2016 Sebastian Pipping <sebastian@pipping.org>
# Licensed under GPL v2 or later

ANT = ant

.PHONY: all
all:
	$(ANT) all

.PHONY: clean
clean:
	$(ANT) clean

.PHONY: install
install:
	$(ANT) install
