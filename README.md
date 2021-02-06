# waze-toll-tool
A tool to populate Waze toll prices. If you don't know what it is then you don't need it.

## Introduction

This tool is intended to fill Entry/Exit matrix for the Waze toll tool.
If you do not know what it is then this tool is useless for you.

It was written for France but could be used for other countries.
It will read various price grids from different companies. Then you give it the toll json file from Waze and it will
build the Entry/Exit price matrix for you.

## Setup

To use that tool, you must provide prices grids from toll companies in a directory structure :

* prices
  * alias.csv
  * COUNTRY
    * flat
    * onedirmatrix
      * cars
      * motorcycles
    * triangle
      * cars
      * motorcycles

Of course COUNTRY is to be replaced with your country

### alias.csv

Contains Toll name aliases (as toll company do not always use the same name for a toll)

#### Example 

```
 DIJON ARC S/TILLE,DIJON ARC SUR TILLE\n
 DIJON-ARC S/TILLE,DIJON ARC SUR TILLE\n
 DIJON-ARC SUR TILLE,DIJON ARC SUR TILLE\n
 DIJON/ARC-SUR-TILLE,DIJON ARC SUR TILLE\n
```

### flat

Will contains a classical flat price matrix :

```
Gare d'entrée	Gare de sortie	Distance	Catégorie 1	Catégorie 2	Catégorie 3	Catégorie 4	Catégorie 5
ALLAINES	AMBERIEU	462,61	50,30	75,80	121,60	163,20	28,10
ALLAINES	ARLAY	403,12	45,30	68,40	111,50	148,50	24,80
ALLAINES	AUXERRE NORD	160,62	25,70	37,60	61,90	80,80	12,90
```

### onedirmatrix

Will contains one direction matrix

```
X	Bifurcation A46S/A46 vers A43 ou A46	SAINT PRIEST CENTRE	MIONS	VENISSIEUX	MARENNES	COMMUNAY	Bifurcation A46S/A7/A47 vers Lyon ou St Etienne	CHASSE SUD	VIENNE SUD	PEAGE DE VIENNE	AUBERIVES	CHANAS	TAIN	VALENCE NORD	VALENCE SUD	LORIOL	MONTELIMAR NORD	MONTELIMAR SUD	BOLLENE	ORANGE NORD	ORANGE	ROQUEMAURE	REMOULINS	NIMES EST	NIMES OUEST	NIMES CENTRE	NIMES GARONS	PEAGE D'ARLES	GALLARGUES	LUNEL
SETE	29,6	29,6	29,6	29,6	29,6	29,6	29,6	29,6	29,6	29,6	25,7	25	22	21,1	21	18,2	16,5	14,9	12,8	10,8	10,2	9,2	7,4	5,4	4,8	4,9	5,6	8,1	3,2	2,8
AGDE PEZENAS	31,7	31,7	31,7	31,7	31,7	31,7	31,7	31,7	31,7	31,7	27,1	27	24	23	22,9	20,1	18,6	16,9	14,8	12,9	12,1	11,2	9,4	7,4	6,8	6,9	7,7	10	5,2	4,8
PEAGE DE BEZIERS CABRIALS	32	32	32	32	32	32	32	32	32	32	28,1	27,4	24,5	23,6	23,4	20,7	19,5	17,8	15,9	13,8	12,9	12	10,4	8,3	7,7	7,8	8,6	10,8	6,1	6
```

### triangle

Will contains triangle matrix

```
LA SAULCE
2,8	SISTERON NORD
3,6	0,9	SISTERON SUD
4,2	1,5	0,6	AUBIGNOSC
5,5	2,8	1,7	1,1	PEYRUIS
6,9	4,4	3,5	2,7	1,5	LA BRILLANNE
8,7	6,2	4,9	4,3	3,1	1,5	MANOSQUE
9,9	7,9	6,4	5,8	4,6	3,1	1,5	SAINT PAUL LEZ DURANCE
11,7	9,9	8,4	7,8	6,8	5,1	3,6	2,1	PERTUIS
```

## Use

Launch it and go to http://127.0.0.1:8080/france.html

Paste your json in the top textarea, get the result in the middle.

The last area is for an audit to warn you for missing ore obsolete prices.

Obsolete prices can be fixed by adding more recent price matrix while missing prices is more tricky.

It can be fixed by adding missing matrix, fixing entry/exit names in your file, or adding aliases in alias.csv.

But some cannot be fixed because in some networks it is possible to go from A to B but not from B to A for a few tolls.

## Extractors

I wrote some extractors to extract prices from pdf to required tsv

- com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.APRRCleaner 