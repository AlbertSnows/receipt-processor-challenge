# FAQ

Answering basic questions

## What's the teck stack

We're using java with the spring boot framework, H2 as the in-memory db

## Isn't that overkill?

For this kind of project? Yes.

## Then why choose this tech stack?

Spring Boot is doing a lot of interesting things with their framework that I want to learn more about. This is as much a
project for evaluation as it is a chance to grokk Spring Boot's framework as a personal learning exercise. This solution
could have just as easily (
actually, probably much more easily) been implemented in Node JS w/ Express, or any other mature API framework.

## Any tradeoffs?

Yeah, a few I imagine. I already mentioned that this is a bit enterprise for a simple rest api, but that was voluntary.
There are also some optimization issues. Some are small and via the data I use, but others are more nuanced. For
example, gradle and docker can just take a while to build things. 