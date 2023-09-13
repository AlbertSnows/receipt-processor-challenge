# What Are State Pairs?

In this project you'll probably notice a few atypical quirks in terms of software design. Examples include the folder
structure (e.g. read/write services), Vavr Trys, and possibly most unusually, State Pairs.

State pairs aren't actually a thing, they're just the manifestation of an idea I've had floating around in my mind for some time. Put simply, they're a way to associate state with some arbitrary outcome; most often represented via pattern matching in other languages (which java doesn't have, hence writing it myself). What follows is a deeper dive into the motivation and implmentation of this data concept. 

Just a heads up, for me personally, I consider the outcome of this project a success. I don't know if I'd use this in a production environment. However, I will be taking the work in this repo and moving it into personal projects for further exploration. The rest of this file will essentially be a reflection on the outcome of this research project. 

Let's look at the motivating problem.

```java
int z=7;
if(z>10) {
  System.out.println("z is greater than 10");
} else if(z< 10){
  System.out.println("z is less than 10");
} else {
  System.out.println("z is equal to 10");
}
```

Question 1: How many different states does this code express?

Answer: 3! Not that complicated.

How about this one?

```java
int age=25;
boolean isStudent=true;

if(age>=21) {
  System.out.println("You are an adult.");
  if(isStudent){
    System.out.println("You are a student.");
  }
}else if(age==20){
  boolean graduatedHS = checkIfGraduatedHS();
  if(isStudent && graduatedHS) {
    System.out.println("You're in college.");
  } else if(!graduatedHS){
    System.out.println("You're still in HS.")		
  }
}else if(age <= 18){
  System.out.println("You are a minor.");
}
```

Question 2: Same one, how many states?

Answer: 7 states! Although some may not be that obvious

Ok, last one. How many states?

```java
int age = 30;
boolean isStudent = false;
boolean hasJob = true;
boolean hasSavings = true;
if(age>=18){
  System.out.println("You are an adult.");
  
  if(isStudent){
    System.out.println("You are a student.");
  
    if(hasSavings){
      System.out.println("You have savings as a student.");
    }else{
      System.out.println("You don't have savings as a student.");
    }
  }else{
    System.out.println("You are not a student.");
  
    if(hasJob){ 
      System.out.println("You have a job.");
    
      if(hasSavings){
        System.out.println("You have savings as a non-student with a job.");
      }else{
        System.out.println("You don't have savings as a non-student with a job.");
      }
    }else{
      System.out.println("You don't have a job.");
    }
  }
}else{
  System.out.println("You are a minor.");
}
```

Answer: 6..? I don't know about you, but I spent more time on this one than the last one, despite the fact it has fewer states(...I think). 

So allow me to make a suggestion. Wouldn't just listing all the states out be easier?
Something like this. 

```java
[
  age >= 18 && isStudent && hasSavings,
  age >= 18 && isStudent && !hasSavings,
  age >= 18 && !isStudent && hasJob && hasSavings,
  age >= 18 && !isStudent && hasJob && !hasSavings,
  age >= 18 && !isStudent && !hasJob,
  age < 18
]
```

How many states is that? 6. You can just count them. 

In my experience as a programmer 

* (L)eft is a boolean
    * (R)ight is any associated value that match all other R's in the list
    * L is meant to represent application state; a possible situation your code can be in
    * R is meant to encompass what you want to accomplish *if* L were true
    * As such, P represents pairs of state => value relationships such that
    * @return is represented by R, the value you want when L is *true*