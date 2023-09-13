# What Are State Pairs?

In this project, you'll probably notice a few atypical quirks in terms of software design. Examples include the folder 
structure (e.g., read/write services), Vavr Trys, and, possibly most unusually, State Pairs.

State pairs aren't actually a thing; they're just the manifestation of an idea I've had floating around in my mind for some time. 
Put simply, they're a way to associate state with some arbitrary outcome, most often represented via pattern matching in 
other languages (which Java doesn't have, hence writing it myself). What follows is a deeper dive into the motivation
and implementation of this data concept.

Just a heads up, personally, I consider the outcome of this project a success. Although, I don't know if I'd use this 
type of code in a production environment. I will, however, be taking the work in this repo and moving it into personal 
projects for further exploration. The rest of this file will essentially be a reflection on the outcome of this research
project and is also somewhat a digression from the actual project scope. If I had a blog, I'd post this there.

Anyways, let's look at the motivating problem.

Question 1: How many different states does this code express?


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

Answer: 3! Not that complicated.

Question 2: Same one, how many states?

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

Answer: 7 states! Although some may not be that obvious

Ok, last one. Question 3: How many states?

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

Answer: 6..? I don't know about you, but I spent more time on this one than the last one, despite the fact it has fewer 
states(...I think). 

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

How many states is that? Six. You can simply count them, or, even better, ask the computer to count them for you. Every 
modern programming language has some mechanism for counting the size of collections. Also, there are actually many other
states here, but they're not relevant to our problem scope. (This has both positive and negative implications outside the
scope of this write-up.)

Let's label the style of control flow in Question 3 as ***if-else-if*** style control flow and the second way as ***list-style***
control flow. In the former, you describe your code in terms of what happens in a given scope, while in the latter, you 
list out all possible scopes and provide something to work with for each. The distinction is subtle but immensely valuable,
and we'll revisit these concepts later.

As computer scientists, we tackle numerous computer problems, but state, in some sense, lies at the core of what we deal
with on a day-to-day basis. This can be summarized by three fundamental questions:

    What information do we care about?
    How do we identify that information?
    How do we change that information?

In most codebases I've worked in, Question 3 from earlier tends to be the focus of my daily tasks. It's not terrible, but
it's not ideal either. You do (sort of) get used to it, though. It is ***the*** conventional way to handle control flow 
in our programs, after all. On a small scale, it works well. Question 1 was easy; it's fairly readable and not too complex.
However, on larger scales, things can quickly become unwieldy.

Now, for a bold assertion: State is essentially a large combinatorics problem. How many different permutations of our data
do we need to consider? This, in my view, forms ***the*** core thesis of a modern software engineer's job. Questions about
how to store, optimize, change, and read data all stem from the problem domain we're working in and connect back to this
fundamental question.

I did pass combinatorics in college, but it wasn't easy. It's not my strong suit, and I'd assume the same holds for my 
peers. Perhaps we're slightly better at counting permutations than the average person, but not significantly. Trying to 
hold multiple potential program states in my mind simultaneously becomes unwieldy after just a few, and most complex 
software systems deal with far more permutations than that. In such cases, why not simplify the process of reasoning about
permutations as much as possible?

I believe this problem arises primarily due to human limitations. Computers are optimized for if-else-if scope matching, 
but in terms of logic, they don't really care how you represent it. The same cannot be said for humans. On a small scale,
if-else-if structures have what I would call a ***readability*** advantage. They often flow like English and are thus easy
to comprehend. However, as systems grow larger, the readability of if-else-if systems begins to provide diminishing returns
in terms of enhancing our reasoning abilities.

The same can be said for lists. "Terms & Conditions" is a giant list of rules that no one ever reads when they use new 
software. However, lists are fundamentally simple. They are quite literally just line after line of stuff. Maybe there's
a lot of stuff, but the manner in which it's conveyed never extends beyond showing you an ever-descending sequence of 
scenarios. Unless you start treating if-else-if like a list by 
[flattening](https://www.refactoring.com/catalog/replaceNestedConditionalWithGuardClauses.html) it, not only do if 
statements grow linearly, but they also expand ***horizontally***, leading to a phenomenon lovingly referred to in our 
community as the [flattening](https://www.refactoring.com/catalog/replaceNestedConditionalWithGuardClauses.html). It 
becomes a two-dimensional problem.

Now that we're at the end, let me show you how I dealt with this in Java. Java doesn't have pattern matching. 
[Scala does,](https://docs.scala-lang.org/tour/pattern-matching.html) but as neat as it looks, I've never used it, nor 
have I met anyone who has. So, how do I accomplish this in Java?

State pairs. My best guess so far is state pairs. What are state pairs? ***State pairs are a specific kind of tuple.*** 
That's it. They specifically take the form Pair<Boolean, O>, where the left-side boolean represents whether or not you're
in that state, and O represents the outcome, representing whatever you want to do in that state. What does that look 
like? Well, if I were to implement the example question 3, it would look something like this:

```java
List.of(Pair.of(age >= 18 && isStudent && hasSavings, List.of("adult","student", "savings")),
        Pair.of(age >= 18 && isStudent && !hasSavings, List.of("adult", "student", "no savings")),
        Pair.of(age >= 18 && !isStudent && hasJob && hasSavings, List.of("adult", "job", "savings")),
        Pair.of(age >= 18 && !isStudent && hasJob && !hasSavings, List.of("adult", "job", "no savings")),
        Pair.of(age >= 18 && !isStudent && !hasJob, List.of("adult", "no job")),
        Pair.of(age < 18, "minor"));
```

I placed a list of keywords on the right side, but keep in mind you can put anything in there. In Question 3, we were 
logging information in several places. Wouldn't it be nice if, instead of having to write System.out.println("blahblahblah")
every time you enter a relevant scope, you could simply describe which states are relevant to which messages, and then 
iterate over a loop where you print every message? Let me show you what I mean.

Suppose we're in the first case. 

`age >= 18 && isStudent && hasSavings`

This has three associated actions.

```java 
System.out.println("You are an adult.");
System.out.println("You are a student.");
System.out.println("You have savings as a student.");
```

Now suppose we make a pair instead. 

```java
Pair.of(age >= 18 && isStudent && hasSavings, 
        List.of("You are an adult",
                "You are a student",
                "You have savings as a student"));
``` 

Now if I can find some way to check the left side of the tuple to see if it's true, then I get the right side and iterate over it. 

```java 
for(String whatAmI : currentState.getSecond()) {
  System.out.println(whatAmI);
}
```
Or you can just use streams in Java, those are cool. 

I have functions like firstTrueStateOf in this repository that retrieve the current state for us. Once you understand 
what state pairs are doing, I believe the rest will fall into place. Therefore, I'm going to conclude here. There are 
many more topics to discuss, such as laziness and memoization for optimization, but while some of these were implemented
for this project, they are beyond the scope of this write-up. Thank you for reading.