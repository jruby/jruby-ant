# Stand-in for the rake-ant gem's RakeWrapper; records each call so the
# Java side of the tasks can be tested without Rake or Ant's Ruby DSL.
class RakeWrapper
  def execute(*args)
    Java::OrgJrubyAnt::Recorder.record('execute', args.to_a, $project)
    raise 'rake aborted!' if args.include?('explode')
  end

  def import(*args)
    Java::OrgJrubyAnt::Recorder.record('import', args.to_a, $project)
  end
end
