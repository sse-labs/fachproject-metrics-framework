library(tidyverse)
library(cli)
library(curl)

#setwd("C:\\Users\\p\\IdeaProjects\\fachproject-metrics-framework")
path_base <- str_replace_all(getwd(),"/","\\\\")
path_base

path_to_app <- str_c(path_base, "\\target\\scala-2.12\\analysis-application.jar")
pathToJars <- "singlefilejars"




singleFileMetricsAll <- c("Initialization","locphy","class.NumberOfFunctions","wmc","MaximumNesting","class.dit","methods.loop","AverageNesting",
                          "class.AVGFanOut","rfc","methods.vref","classes.LCOM","loc.pro","class.NumberOfChildren","lloc","VariablesDeclared.count",
                          "methods.CRef","dac","metric.cbo","mccc")  %>% as_tibble() %>% rename(metric = value) %>% arrange(metric)

#singleFileMetricsAll <- c("Initialization","locphy","class.NumberOfFunctions","MaximumNesting","AverageNesting","class.dit","methods.loop",
#                          "loc.pro","class.NumberOfChildren","lloc")  %>% as_tibble() %>% rename(metric = value) %>% arrange(metric)

singleFileMetricsAll <- c("class.NumberOfChildren","MaximumNesting","class.dit","AverageNesting","loc.pro") %>% as_tibble() %>% rename(metric = value) %>% arrange(metric)
singleFileMetricsAll


#singleFileMetricsGroup1 <- c("Initialization","locphy","class.NumberOfFunctions","wmc","MaximumNesting","class.dit","methods.loop","AverageNesting",
#                             "class.AVGFanOut","rfc","methods.vref","classes.LCOM","loc.pro","class.NumberOfChildren","lloc","VariablesDeclared.count",
#                             "methods.CRef","dac","metric.cbo","mccc")  %>% as_tibble() %>% rename(metric = value) %>% arrange(metric)

multiFileMetricsAll <- c("Evolution","External\ Stability","Internal\ Stability") %>% as_tibble() %>% rename(metric = value) %>% arrange(metric)

multiFileMetricsGroup1 <- c("Evolution","External Stability","Internal Stability") %>% as_tibble() %>% rename(metric = value) %>% arrange(metric)


#multiFileMetricsAll


dir_benchmark <-function()
{
  return (str_c(path_base,"\\src\\benchmark\\R\\group1"))
}


create_dirs <- function()
{
  path_dir <- str_c(dir_benchmark(),"\\jars")
  path_dir
  if(!dir.exists(path_dir))
  {
    dir.create(path_dir)
  }
}

rename_analysis_application <- function ()
{
  path_to_app <- str_c(path_base, "\\target\\scala-2.12\\analysis-application.jar")
  path_to_app_backup <- str_c(path_base, "\\target\\scala-2.12\\analysis-application.jar.backup")

  if(file.exists(path_to_app))
  {
    file.rename(path_to_app,path_to_app_backup )
  }
}

undo_rename_analysis_application <- function ()
{
  path_to_app <- str_c(path_base, "\\target\\scala-2.12\\analysis-application.jar")
  path_to_app_backup <- str_c(path_base, "\\target\\scala-2.12\\analysis-application.jar.backup")

  if(file.exists(path_to_app_backup))
  {
    if(file.exists(path_to_app))
    {
      file.remove(path_to_app)
    }
    file.rename(path_to_app_backup,path_to_app )
  }
}

build_analysis_application <- function()
{
  path_to_app <- str_c(path_base, "\\target\\scala-2.12\\analysis-application.jar")
  path_to_app_backup <- str_c(path_base, "\\target\\scala-2.12\\analysis-application.jar.backup")
  if(.Platform$OS.type == "unix") {
    system("sbt assembly") #, stdout = TRUE, stderr = TRUE)
  } else {
    system("sbt.bat assembly") #, stdout = TRUE, stderr = TRUE)
  }
}





#https://repo1.maven.org/maven2/org/postgresql/postgresql/42.3.3/postgresql-42.3.3.jar
construct_url <- function(url_name,folder, version)
{
  my_url_part1 <- str_c(url_name,folder,version, sep="/")
  my_url_part2 <- str_c(my_url_part1,"/",folder,"-",version,".jar")
  return (my_url_part2)
}

construct_file_path <- function(folder, version, order)
{

  dir_name <- dir_benchmark()
  if(order < 10)
  {
    file_name <- str_glue("0",order,"-","{folder}","-","{version}",".jar")
  } else
  {
    file_name <- str_glue(order,"-","{folder}","-","{version}",".jar")
  }
  my_file_path <- str_glue("{dir_name}\\jars\\{folder}\\{file_name}")
  return (my_file_path)
}

download_postgres <- function()
{
  my_dir_name <- dir_benchmark()
  my_dir_sub_name <- "postgresql"
  my_dir_name_complete <- str_glue("{my_dir_name}\\jars\\{my_dir_sub_name}")

  my_url_name <- "https://repo1.maven.org/maven2/org/postgresql"
  versions <- c("42.3.3","42.3.2","42.3.1","42.3.0","42.2.25",
                "42.2.24","42.2.23","42.2.22","42.2.21","42.2.20")
  versions_rev <- rev(versions)
  if(!dir.exists(my_dir_name_complete))
  {
    cli_text("Create dir: {my_dir_name_complete}")
    dir.create(my_dir_name_complete)
  }

  for(i in seq_along(versions_rev))
  {
    url_current <- construct_url(my_url_name,my_dir_sub_name,versions_rev[i])
    url_current
    file_path <- construct_file_path(my_dir_sub_name,versions_rev[i],i)
    file_path
    #cli_progress_bar(str_glue("Downloading: {url_current}", total = length(versions_rev)))
    if(!file.exists(file_path))
    {
      curl_download(url_current, file_path)
      #cli_progress_update()
    }
  }
}

download_gson <- function()
{
  my_dir_name <- dir_benchmark()
  my_dir_sub_name <- "gson"
  my_dir_name_complete <- str_glue("{my_dir_name}\\jars\\{my_dir_sub_name}")

  my_url_name <- "https://repo1.maven.org/maven2/com/google/code/gson"
  versions <- c("2.9.0","2.8.9","2.8.8","2.8.7","2.8.6",
                "2.8.5","2.8.4","2.8.3","2.8.2","2.8.1")
  versions_rev <- rev(versions)
  if(!dir.exists(my_dir_name_complete))
  {
    dir.create(my_dir_name_complete)
  }

  for(i in seq_along(versions_rev))
  {
    url_current <- construct_url(my_url_name,my_dir_sub_name,versions_rev[i])
    url_current
    file_path <- construct_file_path(my_dir_sub_name,versions_rev[i],i)
    file_path
    if(!file.exists(file_path))
    {
      curl_download(url_current, file_path)
    }
  }
}


download_scala_library <- function()
{
  my_dir_name <- dir_benchmark()
  my_dir_sub_name <- "scala-library"
  my_dir_name_complete <- str_glue("{my_dir_name}\\jars\\{my_dir_sub_name}")

  my_url_name <- "https://repo1.maven.org/maven2/org/scala-lang"
  versions <- c("2.12.15","2.12.14","2.12.13","2.12.12","2.12.11",
                "2.12.10","2.12.9","2.12.8","2.12.7","2.12.6")
  versions_rev <- rev(versions)
  if(!dir.exists(my_dir_name_complete))
  {
    dir.create(my_dir_name_complete)
  }

  for(i in seq_along(versions_rev))
  {
    url_current <- construct_url(my_url_name,my_dir_sub_name,versions_rev[i])
    url_current
    file_path <- construct_file_path(my_dir_sub_name,versions_rev[i],i)
    file_path
    if(!file.exists(file_path))
    {
      curl_download(url_current, file_path)
    }
  }
}

download_kotlin_stdlib <- function()
{
  my_dir_name <- dir_benchmark()
  my_dir_sub_name <- "kotlin-stdlib"
  my_dir_name_complete <- str_glue("{my_dir_name}\\jars\\{my_dir_sub_name}")

  my_url_name <- "https://repo1.maven.org/maven2/org/jetbrains/kotlin"
  versions <- c("1.6.10","1.6.0","1.5.32","1.5.31","1.5.30",
                "1.5.21","1.5.20","1.5.10","1.5.0","1.4.32")
  versions_rev <- rev(versions)
  if(!dir.exists(my_dir_name_complete))
  {
    dir.create(my_dir_name_complete)
  }

  for(i in seq_along(versions_rev))
  {
    url_current <- construct_url(my_url_name,my_dir_sub_name,versions_rev[i])
    url_current
    file_path <- construct_file_path(my_dir_sub_name,versions_rev[i],i)
    file_path
    if(!file.exists(file_path))
    {
      curl_download(url_current, file_path)
    }
  }
}

download_jsoup <- function()
{
  my_dir_name <- dir_benchmark()
  my_dir_sub_name <- "jsoup"
  my_dir_name_complete <- str_glue("{my_dir_name}\\jars\\{my_dir_sub_name}")

  my_url_name <- "https://repo1.maven.org/maven2/org/jsoup"
  versions <- c("1.14.3","1.14.2","1.14.1","1.13.1","1.12.1",
                "1.11.3","1.11.2","1.11.1","1.10.3","1.10.2")
  versions_rev <- rev(versions)
  if(!dir.exists(my_dir_name_complete))
  {
    dir.create(my_dir_name_complete)
  }

  for(i in seq_along(versions_rev))
  {
    url_current <- construct_url(my_url_name,my_dir_sub_name,versions_rev[i])
    url_current
    file_path <- construct_file_path(my_dir_sub_name,versions_rev[i],i)
    file_path
    if(!file.exists(file_path))
    {
      curl_download(url_current, file_path)
    }
  }
}


download_allgemeiner_benchmark <- function ()
{
  file_name <- str_c(dir_benchmark(),"\\allgemeiner-jars.txt")
  data <- read_csv(file_name, col_names =  FALSE)
  data <- data %>% filter(!str_starts(data$X1,"#")) %>% select(X1) %>% rename(url=X1)
  data
  data <- add_column(data,filename = gsub("^.*/","",data$url))
  data$filename
  my_dir_name <- dir_benchmark()
  my_dir_sub_name <- "allgemeiner_benchmark"
  length(data$url)
  my_dir_name_complete <- str_glue("{my_dir_name}\\jars\\{my_dir_sub_name}")
  if(!dir.exists(my_dir_name_complete))
  {
    dir.create(my_dir_name_complete)
  }
  for(i in seq_along(data$url))
  {
    data$url[i]
    file_path <- str_c(my_dir_name_complete,data$filename[i], sep="\\")
    file_path
    if(!file.exists(file_path))
    {
      curl_download(data$url[i], file_path)
    }
  }
}

download_singlefile_benchmark <- function ()
{
  file_name <- str_c(dir_benchmark(),"\\singlefile-jars.txt")
  data <- read_csv(file_name, col_names =  FALSE)
  data <- data %>% filter(!str_starts(data$X1,"#")) %>% select(X1) %>% rename(url=X1)
  data
  data <- add_column(data,filename = gsub("^.*/","",data$url))
  data$filename
  my_dir_name <- dir_benchmark()
  my_dir_sub_name <- "single_benchmark"
  length(data$url)
  my_dir_name_complete <- str_glue("{my_dir_name}\\jars\\{my_dir_sub_name}")
  if(!dir.exists(my_dir_name_complete))
  {
    dir.create(my_dir_name_complete)
  }
  for(i in seq_along(data$url))
  {
    data$url[i]
    file_path <- str_c(my_dir_name_complete,data$filename[i], sep="\\")
    file_path
    if(!file.exists(file_path))
    {
      curl_download(data$url[i], file_path)
    }
  }
}



create_dirs()
rename_analysis_application()
build_analysis_application()
download_postgres()
download_gson()
download_scala_library()
download_kotlin_stdlib()
download_jsoup()
download_allgemeiner_benchmark()
download_singlefile_benchmark()
undo_rename_analysis_application()


allgemeiner_benchmark<-function()
{

  path_benchmark <- str_c(dir_benchmark(),"\\benchmark")
  path_benchmark_allgemeiner <- str_c(path_benchmark,"\\allgemeiner_benchmark")

  if(!dir.exists(path_benchmark))
  {
    dir.create(path_benchmark)
  }
if(!dir.exists(path_benchmark_allgemeiner))
{
  dir.create(path_benchmark_allgemeiner)
}

  path <- str_c(dir_benchmark(),"\\jars\\allgemeiner_benchmark")
  path
  jars <- list.files(path)
  jars
  for(numberOfAnalysis in seq_along(singleFileMetricsAll$metric))
  {

    #generate Metric Data : analysis-'metricname'-'jarname'.csv
    #generate Performance Data: performance-report-'metricname'-'jarname'.csv
    for(jarNumber in seq_along(jars))
    {
      folder <- str_c(path,"\\",singleFileMetricsAll[numberOfAnalysis,1], "\\")
      folder_benchmark <- str_c(path_benchmark_allgemeiner,"\\",singleFileMetricsAll[numberOfAnalysis,1], "\\")

      if (dir.exists(folder_benchmark) == FALSE) {
        dir.create(folder_benchmark)
      }
      myJar <- str_c(path,jars[jarNumber], sep="\\")
      myJar
      myOutFile <- str_c(folder_benchmark,"analysis-", singleFileMetricsAll[numberOfAnalysis,1],"-",  jars[jarNumber], ".csv")
      myOutFileArgument <- str_c("--out-file ", myOutFile)
      mycommand <- str_c(sep= " ","java -jar", path_to_app,"--evaluate-performance", "--is-library","--include-analysis ", singleFileMetricsAll[numberOfAnalysis,1],myOutFileArgument, myJar)
      mycommand
      system(mycommand)
      file.rename("performance-report.csv", str_c(folder_benchmark,"performance-report-",singleFileMetricsAll[numberOfAnalysis,1],"-", jars[jarNumber], ".csv" ))
    }

    print("Ende")

    #generate Performance Data 5 times: performance5-report-'metricname'-'jarname'.csv
    for(jarNumber in seq_along(jars))
    {
      folder <- str_c(path,"\\",singleFileMetricsAll[numberOfAnalysis,1], "\\")
      folder_benchmark <- str_c(path_benchmark_allgemeiner,"\\",singleFileMetricsAll[numberOfAnalysis,1], "\\")

      if (dir.exists(folder_benchmark) == FALSE) {
        dir.create(folder_benchmark)
      }
      myJar <- str_c(path,jars[jarNumber], sep="\\")
      myJar
      myOutFile <- str_c(folder_benchmark,"analysis-", singleFileMetricsAll[numberOfAnalysis,1],"-",jars[jarNumber], ".csv")
      myOutFileArgument <- str_c("--out-file ", myOutFile)
      myOutFileArgument
      mycommand <- str_c(sep= " ","java -jar", path_to_app,"--evaluate-performance", "--is-library","--include-analysis ", singleFileMetricsAll[numberOfAnalysis,1],myOutFileArgument, myJar)
      mycommand
      performance <- tibble()
      for(i in 1:5)
      {
        system(mycommand)
        path_performance <- str_c(path_benchmark_allgemeiner)
        performance_temp <- read_csv("performance-report.csv", na = "/")
        performance_temp <- performance_temp %>% replace(is.na(.), 0)
        performance_temp <- add_column(performance_temp, iteration= i)
        performance_temp
        if(i == 1)
        {
          performance <- performance_temp
        } else {
          performance <- full_join(performance,performance_temp)
        }
      }
      write_csv(performance,str_c(folder_benchmark,"performance5-report-",singleFileMetricsAll[numberOfAnalysis,1],"-", jars[jarNumber], ".csv" ))
    }
    print("Ende")
  }

}

single_benchmark<-function()
{

  path_benchmark <- str_c(dir_benchmark(),"\\benchmark")
  path_benchmark_allgemeiner <- str_c(path_benchmark,"\\single_benchmark")

  if(!dir.exists(path_benchmark))
  {
    dir.create(path_benchmark)
  }
  if(!dir.exists(path_benchmark_allgemeiner))
  {
    dir.create(path_benchmark_allgemeiner)
  }

  path <- str_c(dir_benchmark(),"\\jars\\single_benchmark")
  path
  jars <- list.files(path)
  jars
  for(numberOfAnalysis in seq_along(singleFileMetricsAll$metric))
  {

    #generate Metric Data : analysis-'metricname'-'jarname'.csv
    #generate Performance Data: performance-report-'metricname'-'jarname'.csv
    for(jarNumber in seq_along(jars))
    {
      folder <- str_c(path,"\\",singleFileMetricsAll[numberOfAnalysis,1], "\\")
      folder_benchmark <- str_c(path_benchmark_allgemeiner,"\\",singleFileMetricsAll[numberOfAnalysis,1], "\\")

      if (dir.exists(folder_benchmark) == FALSE) {
        dir.create(folder_benchmark)
      }
      myJar <- str_c(path,jars[jarNumber], sep="\\")
      myJar
      myOutFile <- str_c(folder_benchmark,"analysis-", singleFileMetricsAll[numberOfAnalysis,1],"-",  jars[jarNumber], ".csv")
      myOutFileArgument <- str_c("--out-file ", myOutFile)
      mycommand <- str_c(sep= " ","java -jar", path_to_app,"--evaluate-performance", "--is-library","--include-analysis ", singleFileMetricsAll[numberOfAnalysis,1],myOutFileArgument, myJar)
      mycommand
      system(mycommand)
      file.rename("performance-report.csv", str_c(folder_benchmark,"performance-report-",singleFileMetricsAll[numberOfAnalysis,1],"-", jars[jarNumber], ".csv" ))
    }

    print("Ende")

    #generate Performance Data 5 times: performance5-report-'metricname'-'jarname'.csv
    for(jarNumber in seq_along(jars))
    {
      folder <- str_c(path,"\\",singleFileMetricsAll[numberOfAnalysis,1], "\\")
      folder_benchmark <- str_c(path_benchmark_allgemeiner,"\\",singleFileMetricsAll[numberOfAnalysis,1], "\\")

      if (dir.exists(folder_benchmark) == FALSE) {
        dir.create(folder_benchmark)
      }
      myJar <- str_c(path,jars[jarNumber], sep="\\")
      myJar
      myOutFile <- str_c(folder_benchmark,"analysis-", singleFileMetricsAll[numberOfAnalysis,1],"-",jars[jarNumber], ".csv")
      myOutFileArgument <- str_c("--out-file ", myOutFile)
      myOutFileArgument
      mycommand <- str_c(sep= " ","java -jar", path_to_app,"--evaluate-performance", "--is-library","--include-analysis ", singleFileMetricsAll[numberOfAnalysis,1],myOutFileArgument, myJar)
      mycommand
      performance <- tibble()
      for(i in 1:5)
      {
        system(mycommand)
        path_performance <- str_c(path_benchmark_allgemeiner)
        performance_temp <- read_csv("performance-report.csv", na = "/")
        performance_temp <- performance_temp %>% replace(is.na(.), 0)
        performance_temp <- add_column(performance_temp, iteration= i)
        performance_temp
        if(i == 1)
        {
          performance <- performance_temp
        } else {
          performance <- full_join(performance,performance_temp)
        }
      }
      write_csv(performance,str_c(folder_benchmark,"performance5-report-",singleFileMetricsAll[numberOfAnalysis,1],"-", jars[jarNumber], ".csv" ))
    }
    print("Ende")
  }

}

multiFileMetricsAll <- c("Evolution","\"External Stability\"","\"Internal Stability\"") %>% as_tibble() %>% rename(metric = value) %>% arrange(metric)
multiFileMetricsAllFile <- c("Evolution","External","Internal") %>% as_tibble() %>% rename(metric = value) %>% arrange(metric)
multiFileJars <- c("gson","jsoup","kotlin-stdlib","postgresql","scala-library")

multi_benchmark<-function()
{


  for(jar in seq_along(multiFileJars))
  {
  path_benchmark <- str_c(dir_benchmark(),"\\benchmark")
  path_benchmark_allgemeiner <- str_c(path_benchmark,"\\", multiFileJars[jar])

  if(!dir.exists(path_benchmark))
  {
    dir.create(path_benchmark)
  }
  if(!dir.exists(path_benchmark_allgemeiner))
  {
    dir.create(path_benchmark_allgemeiner)
  }

  path <- str_c(dir_benchmark(),"\\jars\\",multiFileJars[jar])
  path
  path_csv <- str_c(dir_benchmark(),"\\benchmark\\",multiFileJars[jar])
  path_csv

  myOutFile <- str_c(path_csv,"\\analysis-loc.pro.csv")
  myOutFileArgument <- str_c("--out-file ", myOutFile)
  mycommand <- str_c(sep= " ","java -jar", path_to_app,"--batch-mode","--include-analysis loc.pro","--is-library", myOutFileArgument, path)
  mycommand
  system(mycommand)


  for(numberOfAnalysis in seq_along(multiFileMetricsAll$metric))
  {
      folder <- str_c(path,"\\",multiFileMetricsAllFile$metric[numberOfAnalysis], "\\")
      folder
      folder_benchmark <- str_c(path_benchmark_allgemeiner,"\\",multiFileMetricsAllFile$metric[numberOfAnalysis], "\\")
      folder_benchmark

      if (dir.exists(folder_benchmark) == FALSE) {
        dir.create(folder_benchmark)
      }

    myOutFile <- str_c(folder_benchmark,"analysis-", multiFileMetricsAllFile$metric[numberOfAnalysis], ".csv")
    myOutFileArgument <- str_c("--out-file ", myOutFile)
    mycommand <- str_c(sep= " ","java -jar", path_to_app,"--evaluate-performance","--include-analysis ", multiFileMetricsAll$metric[1],  "--multi-file","--is-library", myOutFileArgument, path)
    mycommand
    system(mycommand)
    tibble_performance <- read_csv("performance-report.csv")
    tibble_performance
    for(i in 2:5)
      {
        myOutFile <- str_c(folder_benchmark,"analysis-", multiFileMetricsAllFile$metric[numberOfAnalysis], ".csv")
        myOutFileArgument <- str_c("--out-file ", myOutFile)
        mycommand <- str_c(sep= " ","java -jar", path_to_app,"--evaluate-performance","--include-analysis ", multiFileMetricsAll$metric[1],  "--multi-file","--is-library", myOutFileArgument, path)
        mycommand
        system(mycommand)
        tibble_performance_temp <- read_csv("performance-report.csv")
        tibble_performance <- bind_rows(tibble_performance, tibble_performance_temp)
      }
    write_csv(tibble_performance, str_c(folder_benchmark,"performance-report-",multiFileMetricsAllFile$metric[numberOfAnalysis],".csv"))
    csv_for_markdown(jar,numberOfAnalysis)
    tibble_performance
    }
  }
    print("Ende")

  }

csv_for_markdown<-function(jar,numberOfAnalysis)
{
  path_csv <- str_c(dir_benchmark(),"\\benchmark\\",multiFileJars[jar])
  path_csv
  path_benchmark <- str_c(dir_benchmark(),"\\benchmark")
  path_benchmark_allgemeiner <- str_c(path_benchmark,"\\", multiFileJars[jar])
tibble_performance     <- read_csv(str_c(path_csv,"\\",multiFileMetricsAllFile$metric[numberOfAnalysis],"\\performance-report-",multiFileMetricsAllFile$metric[numberOfAnalysis],".csv"), na=c("/"))
tibble_loc     <- read_csv(str_c(path_benchmark_allgemeiner,"\\analysis-loc.pro.csv"), na=c("/"))
tibble_loc <- tibble_loc %>% rename (File=Path)
tibble_loc
tibble_performance <- add_column(tibble_performance,Ext= replace_na(tibble_performance$`External Stability`,0))
tibble_performance

test <- tibble_performance %>% group_by(File) %>% summarise(mean =mean(Ext), var = var(Ext), sd = sd(Ext))
test2 <- full_join(test,tibble_loc)
test2 <- add_column(test2,loc= replace_na(test2$loc.pro,0)) %>% select(File,mean,var, sd, loc) %>% filter(str_detect(File,"jar"))
test2 <- test2 %>%  mutate(test2,filename = gsub("^.*\\\\","",File)) %>% select(filename,mean,var, sd, loc)
write_csv(test2,str_c(path_csv,"\\",multiFileMetricsAllFile$metric[numberOfAnalysis],"\\table-",multiFileMetricsAllFile$metric[numberOfAnalysis],".csv"))
}



single_benchmark()
allgemeiner_benchmark()
multi_benchmark()







