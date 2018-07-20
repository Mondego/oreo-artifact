Precision folder contains the files used in the Precision experiment of the 5 tools: Oreo, SourcererCC (Scc), CloneWorks, Deckard, and NiCad. 
You will find following folders inside this folder:

-The folder named inputfiles, contain the 400 clone pairs for each tool. These clone pairs are sampled from the clonepairs reported by the tools during the Recall Experiment carried out using BigCloneEval.
-The folder judge_1 contains the judgements of Judge 1. You will find a folder for each tool named after the tool. Inside these folders you will find the inputfile, true_positives file, and the false positives file generated during the precision experiment.
-The folder judge_2 contains the judgements of Judge 2. You will find a folder for each tool named after the tool. Inside these folders you will find the inputfile, true_positives file, and the false positives file generated during the precision experiment.
-We consolidated the false positives of both judges(for each tool) and then the judges went through these consolidated false positives again to resolve the conflicts. The results of this part of the expeirment are inside the consolidated_fps_from_both_judges folder.


