library(tidyverse)
library(cli)
library(curl)

#setwd("C:\\Users\\p\\IdeaProjects\\fachproject-metrics-framework")
path_base <- str_replace_all(getwd(),"/","\\\\")
path_base

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

  for(i in seq_along(versions))
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

  for(i in seq_along(versions))
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

  for(i in seq_along(versions))
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

  for(i in seq_along(versions))
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



path_to_app <- str_c(path_base, "\\target\\scala-2.12\\analysis-application.jar")
pathToJars <- "singlefilejars"

jarUrls <-

jarNames <- c("ant-1.10.12.jar","clickhouse-client-0.3.2.jar","clojure-1.10.3.jar","commons-io-2.11.0.jar","gradle-2.3.0.jar", "graphql-java-17.3.jar",
              "gson-2.8.9.jar", "guava-31.0.1-jre.jar", "httpclient-4.5.13.jar", "itext-4.2.1.jar", "jackson-core-2.13.1.jar", "jackson-databind-2.13.1.jar",
              "jsoup-1.14.3.jar", "junit-4.13.2.jar", "kotlin-stdlib-1.6.10.jar", "maven-core-3.8.4.jar", "pcollections-3.1.4.jar", "pdfbox-2.0.25.jar",
              "rhino-1.7.14.jar", "scala-library-2.13.8.jar") %>% as_tibble() %>% add_column(pathToJars) %>% rename(JAR = value) %>% arrange(JAR) %>% add_column(ID = 1:20)

singleFileMetricsAll <- c("Initialization","locphy","class.NumberOfFunctions","wmc","MaximumNesting","class.dit","methods.loop","AverageNesting",
                          "class.AVGFanOut","rfc","methods.vref","classes.LCOM","loc.pro","class.NumberOfChildren","lloc","VariablesDeclared.count",
                          "methods.CRef","dac","metric.cbo","mccc")  %>% as_tibble() %>% rename(metric = value) %>% arrange(metric)

singleFileMetricsGroup1 <- c("Initialization","locphy","class.NumberOfFunctions","wmc","MaximumNesting","class.dit","methods.loop","AverageNesting",
                             "class.AVGFanOut","rfc","methods.vref","classes.LCOM","loc.pro","class.NumberOfChildren","lloc","VariablesDeclared.count",
                             "methods.CRef","dac","metric.cbo","mccc")  %>% as_tibble() %>% rename(metric = value) %>% arrange(metric)

multiFileMetricsAll <- c("Evolution","External Stability","Internal Stability") %>% as_tibble() %>% rename(metric = value) %>% arrange(metric)

multiFileMetricsGroup1 <- c("Evolution","External Stability","Internal Stability") %>% as_tibble() %>% rename(metric = value) %>% arrange(metric)

singleFileMetricsAll

for(numberOfAnalysis in 1:20)
{
  #generate Metric Data : analysis-'metricname'-'jarname'.csv
  #generate Performance Data: performance-report-'metricname'-'jarname'.csv
  for(jarNumber in 3:3)
  {
    folder <- str_c(singleFileMetricsAll[numberOfAnalysis,1], "\\")
    if (dir.exists(folder) == FALSE) {
      dir.create(folder)
    }
    myJar <- str_c(pathToJars,jarNames[jarNumber,1], sep="\\")
    myJar
    myOutFile <- str_c(folder,"analysis-", singleFileMetricsAll[numberOfAnalysis,1],"-",  select(jarNames[jarNumber,],JAR), ".csv")
    myOutFileArgument <- str_c("--out-file ", myOutFile)
    mycommand <- str_c(sep= " ","java -jar", pathToAnalysisApplication,"--evaluate-performance", "--is-library","--include-analysis ", singleFileMetricsAll[numberOfAnalysis,1],myOutFileArgument, myJar)
    mycommand
    system(mycommand)
    file.rename("performance-report.csv", str_c(folder,"performance-report-",singleFileMetricsAll[numberOfAnalysis,1],"-",select(jarNames[jarNumber,],JAR), ".csv" ))
  }

  print("Ende")

  #generate Performance Data 5 times: performance5-report-'metricname'-'jarname'.csv
  for(jarNumber in 3:3)
  {
    folder <- str_c(singleFileMetricsAll[numberOfAnalysis,1], "\\")
    if (dir.exists(folder) == FALSE) {
      dir.create(folder)
    }
    myJar <- str_c(pathToJars,jarNames[jarNumber,1], sep="\\")
    myJar
    myOutFile <- str_c(folder,"analysis-", singleFileMetricsAll[numberOfAnalysis,1],"-",  select(jarNames[jarNumber,],JAR), ".csv")
    myOutFileArgument <- str_c("--out-file ", myOutFile)
    mycommand <- str_c(sep= " ","java -jar", pathToAnalysisApplication,"--evaluate-performance", "--is-library","--include-analysis ", singleFileMetricsAll[numberOfAnalysis,1],myOutFileArgument, myJar)
    mycommand
    performance <- tibble()
    for(i in 1:5)
    {
      system(mycommand)
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
    write_csv(performance,str_c(folder,"performance5-report-",singleFileMetricsAll[numberOfAnalysis,1],"-",select(jarNames[jarNumber,],JAR), ".csv" ))
  }
  print("Ende")
}





