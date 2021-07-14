@echo off
::See if quotations are needed below
rd /s /q data/databases/*
ECHO data/databases cleared
rd /s /q data/transactions/*
ECHO data/transactions cleared
ECHO Attempting imports
bin/neo4j-admin import --nodes=Semantic="import/TUIs.csv" --nodes=Concept="import/CUIs.csv" --nodes=Code="import/CODEs.csv" --nodes=Term="import/SUIs.csv" --nodes=Definition="import/DEFs.csv" --nodes=NDC="import/NDCs.csv" --relationships=ISA_STY="import/TUIrel.csv" --relationships=STY="import/CUI-TUIs.csv" --relationships="import/CUI-CUIs.csv" --relationships=CODE="import/CUI-CODEs.csv" --relationships="import/CODE-SUIs.csv" --relationships=PREF_TERM="import/CUI-SUIs.csv" --relationships=DEF="import/DEFrel.csv" --relationships=NDC="import/NDCrel.csv" --skip-bad-relationships --skip-duplicate-nodes
ECHO First import statement complete
bin/neo4j-admin import --nodes=Concept="import/CUIs.csv" --nodes=Code="import/CODEs.csv" --nodes=Term="import/SUIs.csv" --relationships="import/CUI-CUIs.csv" --relationships=CODE="import/CUI-CODEs.csv" --relationships="import/CODE-SUIs.csv" --skip-bad-relationships --skip-duplicate-nodes
ECHO Second import statement complete
ECHO Properly imported! Graph setup complete