# What Are State Pairs?

In this project you'll probably notice a few atypical quirks in terms of software design. Examples include the folder
structure (e.g. read/write services), Vavr Trys, and possibly most unusually, State Pairs.

State pairs aren't actually a thing, they're just the manifestation of an idea I've had floating around in my mind for some time. Put simply, they're a way to associate state with some arbitrary outcome; most often represented via pattern matching in other languages (which java doesn't have, hence writing it myself). What follows is a deeper dive into the motivation and implmentation of this data concept. 

Just a heads up, for me personally, I consider the outcome of this project a success. Although, I don't know if I'd use this type of code in a production environment. I will, however, be taking the work in this repo and moving it into personal projects for further exploration. The rest of this file will essentially be a reflection on the outcome of this research project, and is also somewhat a digression from the actual project scope. If I had a blog I'd post this there. 

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

How many states is that? 6. You can just count them. Or, better yet, you can ***ask the computer to count them for you.*** Every modern language has some notion of counting the size of collections. Also, There are actually *many* other states here, but they're *not relevant* to our problem scope. (This has good and bad implications outside the scope of this writeup)

Let's call Question 3 ***if-else-if*** style control flow and the second way ***list*** style control flow. In the former, you talk about your code in terms of what happens in a given scope, in the latter you list out all possible scopes and give me something to work with to go with it. The distinction is subtle, but immensely valuable. We'll come back to these concepts later. 

As computer scientists, we solve many computer problems, but state in *some sense* is 
at the core of what we deal with on a day-to-day basis. This can be encompassed by three core questions: 
1) What information do we care about?
2) How do we identify that information?
3) How do we change that information? 

In most codebases I've worked in, Question 3 is about the average of what I look
at on a daily basis. Not terrible, but not great. You do (sort of) get used to it though. It is ***the*** standard way to handle control flow in our programs after all. On small scales, this is great. Question 1 was easy; it's fairly readable and not that complicated. On larger scales? Things get out of hand quickly. 

Ok now for a bold(?) take. State is just one large combinatorics problem. How many different permutations of our data do we need to worry about? That is ***THE*** thesis of a modern SWE's job in my mind. Questions about how to store, optimize, change, and read data all arise out of the problem domain we're working in and relate back to this question. 

I passed combinatorics in college, but it wasn't easy. It's not my strong suite and I'd assume just as much for my peers. Maybe we're better at counting permutations than the average Joe, but not by much. Keeping multiple possible program states in my head at once becomes unwieldy after just a handful, and most complex software systems are dealing with far, *far* more relevant permutations than that. In which case, why not make reasoning about permutations as simple as possible? 

I think the reason this problem manifests is just due to human quirks. Computers are optimized for if-else-if scope matching, but in terms of logic they don't really care how you represent it. The same cannot be said for humans. On small scales, if-else-if has what I would call a ***readable*** advantage. It typically flows like english and is thus easy to ***reason*** about. On larger scales though, the readability of if-else-if systems begins to see diminishing returns on how much it contributes to are reasoning ability. 

The same can be said for lists too. Terms & Conditions are a giant list of rules that no one ever reads when they use a new piece of software. However, lists are fundamentally simple. They are quite literally just line after line of stuff. Maybe there's a lot of stuff, but the manner in which it's conveyed never extends beyond just showing you an ever-descending sequence of scenarios. Unless you start treating `if-else-if` like a list by [flattening](https://www.refactoring.com/catalog/replaceNestedConditionalWithGuardClauses.html) then not only do if statements grow linearly, they grow ***horizontally*** as well in a phenomenon that is lovingly referred to in our community as [the pyramid of doom](https://en.wikipedia.org/wiki/Pyramid_of_doom_(programming)). It becomes a two-dimensional problem. 

Ok, now that we're at the end, let me show you how I dealt with this in java. Java doesn't have pattern matching. [Scala does,](https://docs.scala-lang.org/tour/pattern-matching.html) but as neat as it looks I've never used it nor do have I met anyone who has either. So how do I accomplish this in Java? 

State pairs. My best guess so far is state pairs. What are state pairs? ***State pairs are a specific kind of tuple.*** That's it. They specifically take the form 
Pair<Boolean, O> where the left side boolean represents whether or not you're in that state and O = outcome and represents whatever you want to do in that state. What does that look like? Well, if I were to implement the example question 3, it would look something like this. 

```java
List.of(Pair.of(age >= 18 && isStudent && hasSavings, List.of("adult","student", "savings")),
        Pair.of(age >= 18 && isStudent && !hasSavings, List.of("adult", "student", "no savings")),
        Pair.of(age >= 18 && !isStudent && hasJob && hasSavings, List.of("adult", "job", "savings")),
        Pair.of(age >= 18 && !isStudent && hasJob && !hasSavings, List.of("adult", "job", "no savings")),
        Pair.of(age >= 18 && !isStudent && !hasJob, List.of("adult", "no job")),
        Pair.of(age < 18, "minor"));
```

I put a list of keywords in the right side, but keep in mind you can put ***anything*** in there. In Question 3 we were logging info in a bunch of places. Wouldn't it be nice if, instead of having to write `System.out.println("blahblahblah")` every time you enter a relevant scope you could just ***describe*** what states are relevant to which messages, and then just iterate over a loop where you println every message? Let me show you what I mean. 

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

I have functions such as `firstTrueStateOf` in this repo that get `currentState` for us. Once you understand what state pairs are doing, I think the rest falls into place. As such, I'm going to stop here. There are many more things to talk about, such as laziness and memoization for optimization, but while some of that was implemented for this project, it's outside the scope of this writeup. Thanks for reading. 
