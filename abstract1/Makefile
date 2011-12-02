TEXFILE=exercise1.tex
PDFFILE=exercise1.pdf

all: everything

everything:
	make pdf
	make pdf

pdf:
	pdflatex ${TEXFILE}

e: edit
edit:
	vim ${TEXFILE}

v: view
view:
	okular ${PDFFILE} 2>/dev/null 1>&2 &
