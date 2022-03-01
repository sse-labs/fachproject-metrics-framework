from os import stat
from glob import glob

import pandas as pd
import matplotlib.pyplot as plt


# path to folder with jars
JARFOLDER = "./jars"
# path to folder with csv files
REPORTS = "./reports"
# ouput directory
DIROUT = "./plots"

# descriptions of plots we want to draw
plots = [
    {"title": "LCOM", "x": "FileSize", "y": "classes.LCOM"},
    {"title": "LCOM", "x": "project.codeSize", "y": "classes.LCOM"},
    {"title": "LCOM", "x": "class.count", "y": "classes.LCOM"},
    {"title": "CREF", "x": "FileSize", "y": "methods.CRef"},
    {"title": "CREF", "x": "project.codeSize", "y": "methods.CRef"},
    {"title": "CREF", "x": "class.count", "y": "methods.CRef"},
    {"title": "AVGFanIn", "x": "FileSize", "y": "class.AVGFanIn"},
    {"title": "AVGFanIn", "x": "project.codeSize", "y": "class.AVGFanIn"},
    {"title": "AVGFanIn", "x": "class.count", "y": "class.AVGFanIn"},
    {"title": "AVGFanOut", "x": "FileSize", "y": "class.AVGFanOut"},
    {"title": "AVGFanOut", "x": "project.codeSize", "y": "class.AVGFanOut"},
    {"title": "AVGFanOut", "x": "class.count", "y": "class.AVGFanOut"},
    {"title": "NumberOfFunctions", "x": "FileSize", "y": "class.NumberOfFunctions"},
    {"title": "NumberOfFunctions", "x": "project.codeSize", "y": "class.NumberOfFunctions"},
    {"title": "NumberOfFunctions", "x": "method.count", "y": "class.NumberOfFunctions"}
]


# build a matrix with name and size entries for all used jar files
df_filesize = pd.DataFrame(
    [
        (jar, stat(jar).st_size // 1024)
        for jar in glob(JARFOLDER + "/*.jar")
    ],
    columns=["File", "FileSize"],
)

# read additional info about the jars: #classes, #methods, code size
df_projectinfo = pd.read_csv("classes-methods-codesize.csv")
df_projectinfo.rename(columns={"Path": "File"}, inplace=True)

# read all performance reports into one dataframe
df_performance = pd.concat(
    [
        pd.read_csv(report, skiprows=[1])
        for report in glob(REPORTS + "/performance-report-*.csv")
    ]
)
# calculate averages
df_performance = df_performance.groupby("File", as_index=False).mean()

# normalize values in "File" column (i.e. keep only filename, remove path if any)
df_filesize["File"] = df_filesize["File"].str.split("/").str.get(-1)
df_projectinfo["File"] = df_projectinfo["File"].str.split("/").str.get(-1)
df_performance["File"] = df_performance["File"].str.split("/").str.get(-1)

# merge everything into one matrix ("inner join")
df = pd.merge(df_filesize, df_projectinfo, on=["File"], how="inner")
df = pd.merge(df, df_performance, on=["File"], how="inner")

# draw some plots
for plot in plots:
    xlabel = "File Size [KBytes]" if plot["x"] == "FileSize" else plot["x"]
    ylabel = "Time [msecs]"
    fig, ax = plt.subplots(figsize=(10.24, 7.68))
    ax.scatter(x=df[plot["x"]], y=df[plot["y"]], color="royalblue")
    ax.set_xlabel(plot.get("xlabel", xlabel), fontsize=13, labelpad=10)
    ax.set_ylabel(plot.get("ylabel", ylabel), fontsize=13, labelpad=10)
    ax.set_title(plot["title"], fontsize=14, pad=20)
    plt.savefig("{}/{}-{}.png".format(DIROUT, plot["y"], plot["x"]))
    plt.close(fig)
