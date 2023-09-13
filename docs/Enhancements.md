# Enhancements

This document is to keep track of proposed improvements to the behavior of this repo

## Validation

### Check total

We don't currently check if total of receipt matches item.
To do this, we'd need to add an additional check by extracting the sums
of the items and comparing it to the total.

## Optimization

In the state pairs, I should probably double check that functions called inside the suppliers aren't eagerly evaluated

## Error Handling

We don't currently handle bulk queries.
The code is (sort of) there, but it would
take more work to get set up

## Design

### Finite State Machines (FSM)

Every situation has a state and an outcome.
Why not also a unique identifier?
To solve this assessment, I ended up not needing to use FSM, but they do seem like a more robust, long term solution via
easy access to unique (hashable?) state identifiers. Might be worth checking out.

### FirstStateOF

Currently there's a Collections function that gets the first true state out of a list. Is that necessary? Java has
streams, and anyMatch may be a viable alternative. Or something similar...

## Documentation

I need to add a swagger/open api interface
at some point.